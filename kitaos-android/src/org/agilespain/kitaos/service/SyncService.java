/*
 * Copyright 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.agilespain.kitaos.service;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.format.DateUtils;
import android.util.Log;
import org.agilespain.kitaos.R;
import org.agilespain.kitaos.provider.KitaosContract;
import org.agilespain.kitaos.widget.DownloadAvatarAsyncTask;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

/**
 * Background {@link android.app.Service} that synchronizes data living in
 * {@link org.agilespain.kitaos.provider.KitaosProvider}.
 * Reads data from remote sources
 */
public class SyncService extends IntentService {
    private static final String TAG = "SyncService";

    public static final String EXTRA_STATUS_RECEIVER = "aos.extra.STATUS_RECEIVER";
    public static final String EXTRA_FORCE_RELOAD = "aos.extra.EXTRA_FORCE_RELOAD";

    public static final int STATUS_RUNNING = 0x1;
    public static final int STATUS_ERROR = 0x2;
    public static final int STATUS_FINISHED = 0x3;

    private static final int SECOND_IN_MILLIS = (int) DateUtils.SECOND_IN_MILLIS;

    /**
     * Root worksheet feed for online data source
     */


    private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    private static final String ENCODING_GZIP = "gzip";

    private static final int VERSION_NONE = 0;
    private static final int VERSION_CURRENT = 5;

    // private LocalExecutor mLocalExecutor;
    private RemoteExecutor mRemoteExecutor;

    public SyncService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        final HttpClient httpClient = getHttpClient(this);
        final ContentResolver resolver = getContentResolver();

        // mLocalExecutor = new LocalExecutor(getResources(), resolver);
        mRemoteExecutor = new RemoteExecutor(httpClient, resolver);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent(intent=" + intent.toString() + ")");

        final ResultReceiver receiver = intent.getParcelableExtra(EXTRA_STATUS_RECEIVER);
        if (receiver != null) receiver.send(STATUS_RUNNING, Bundle.EMPTY);

        final Context context = this;
        final SharedPreferences prefs = getSharedPreferences(Prefs.IOSCHED_SYNC,
                Context.MODE_PRIVATE);
        final int localVersion = prefs.getInt(Prefs.LOCAL_VERSION, VERSION_NONE);

        final boolean force = intent.getBooleanExtra(EXTRA_FORCE_RELOAD, false);

        try {
            // Bulk of sync work, performed by executing several fetches from
            // local and online sources.

            final long startLocal = System.currentTimeMillis();
            final boolean localParse = localVersion < VERSION_CURRENT;
            Log.d(TAG, "found localVersion=" + localVersion + " and VERSION_CURRENT="
                    + VERSION_CURRENT);

            if (localParse || force) {
                // Always hit remote spreadsheet for any updates
                mRemoteExecutor
                        .executeGet(getString(R.string.url_api_talks), new TalksJsonHandler());

                downloadAvatars();
                // Save local parsed version
                prefs.edit().putInt(Prefs.LOCAL_VERSION, VERSION_CURRENT).commit();
            }
            Log.d(TAG, "local sync took " + (System.currentTimeMillis() - startLocal) + "ms");


            final long startRemote = System.currentTimeMillis();
            Log.d(TAG, "remote sync took " + (System.currentTimeMillis() - startRemote) + "ms");

        } catch (Exception e) {
            Log.e(TAG, "Problem while syncing", e);

            if (receiver != null) {
                // Pass back error to surface listener
                final Bundle bundle = new Bundle();
                bundle.putString(Intent.EXTRA_TEXT, e.toString());
                receiver.send(STATUS_ERROR, bundle);
            }
        }

        // Announce success to any surface listener
        Log.d(TAG, "sync finished");
        if (receiver != null) receiver.send(STATUS_FINISHED, Bundle.EMPTY);
    }

    private void downloadAvatars() {
        String[] projection = new String[]{
                "DISTINCT " + KitaosContract.Talks.SPEAKER_EMAIL
        };
        Cursor avatars = getContentResolver().query(KitaosContract.Talks.uri(), projection, null, null, null);

        while (avatars.moveToNext()) {
            String email = avatars.getString(0);
            (new DownloadAvatarAsyncTask(this)).execute(email);
        }
        avatars.close();

    }

    /**
     * Generate and return a {@link org.apache.http.client.HttpClient} configured for general use,
     * including setting an application-specific user-agent string.
     */
    private static HttpClient getHttpClient(Context context) {
        final HttpParams params = new BasicHttpParams();

        // Use generous timeouts for slow mobile networks
        HttpConnectionParams.setConnectionTimeout(params, 20 * SECOND_IN_MILLIS);
        HttpConnectionParams.setSoTimeout(params, 20 * SECOND_IN_MILLIS);

        HttpConnectionParams.setSocketBufferSize(params, 8192);
        HttpProtocolParams.setUserAgent(params, buildUserAgent(context));

        final DefaultHttpClient client = new DefaultHttpClient(params);

        client.addRequestInterceptor(new HttpRequestInterceptor() {
            public void process(HttpRequest request, HttpContext context) {
                // Add header to accept gzip content
                if (!request.containsHeader(HEADER_ACCEPT_ENCODING)) {
                    request.addHeader(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
                }
            }
        });

        client.addResponseInterceptor(new HttpResponseInterceptor() {
            public void process(HttpResponse response, HttpContext context) {
                // Inflate any responses compressed with gzip
                final HttpEntity entity = response.getEntity();
                final Header encoding = entity.getContentEncoding();
                if (encoding != null) {
                    for (HeaderElement element : encoding.getElements()) {
                        if (element.getName().equalsIgnoreCase(ENCODING_GZIP)) {
                            response.setEntity(new InflatingEntity(response.getEntity()));
                            break;
                        }
                    }
                }
            }
        });

        return client;
    }

    /**
     * Build and return a user-agent string that can identify this application
     * to remote servers. Contains the package name and version code.
     */
    private static String buildUserAgent(Context context) {
        try {
            final PackageManager manager = context.getPackageManager();
            final PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);

            // Some APIs require "(gzip)" in the user-agent string.
            return info.packageName + "/" + info.versionName
                    + " (" + info.versionCode + ") (gzip)";
        } catch (NameNotFoundException e) {
            return null;
        }
    }

    /**
     * Simple {@link org.apache.http.entity.HttpEntityWrapper} that inflates the wrapped
     * {@link org.apache.http.HttpEntity} by passing it through {@link java.util.zip.GZIPInputStream}.
     */
    private static class InflatingEntity extends HttpEntityWrapper {
        public InflatingEntity(HttpEntity wrapped) {
            super(wrapped);
        }

        @Override
        public InputStream getContent() throws IOException {
            return new GZIPInputStream(wrappedEntity.getContent());
        }

        @Override
        public long getContentLength() {
            return -1;
        }
    }

    private interface Prefs {
        String IOSCHED_SYNC = "iosched_sync";
        String LOCAL_VERSION = "local_version";
    }
}
