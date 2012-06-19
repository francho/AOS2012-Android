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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

import android.content.ContentResolver;

/**
 * Executes an {@link org.apache.http.client.methods.HttpUriRequest} and passes the result as an
 * {@link org.json} to the given {@link JsonHandler}.
 */
class RemoteExecutor {
    private final HttpClient mHttpClient;
    private final ContentResolver mResolver;

    public RemoteExecutor(HttpClient httpClient, ContentResolver resolver) {
        mHttpClient = httpClient;
        mResolver = resolver;
    }

    /**
     * Execute a {@link org.apache.http.client.methods.HttpGet} request, passing a valid response through
     * {@link XmlHandler#parseAndApply(org.xmlpull.v1.XmlPullParser, android.content.ContentResolver)}.
     */
    public void executeGet(String url, JsonHandler handler) throws HandlerException {
        final HttpUriRequest request = new HttpGet(url);
        execute(request, handler);
    }

    void execute(HttpUriRequest request, JsonHandler handler) throws HandlerException {
        try {
            final HttpResponse resp = mHttpClient.execute(request);
            final int status = resp.getStatusLine().getStatusCode();
            if (status != HttpStatus.SC_OK) {
                throw new HandlerException("Unexpected server response " + resp.getStatusLine()
                        + " for " + request.getRequestLine());
            }

            final InputStream input = resp.getEntity().getContent();
            try {

                BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                StringBuilder sb = new StringBuilder();
                String line = null;
                try {
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                } finally {
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                String jsonString = sb.toString();

                handler.parseAndApply(jsonString, mResolver);
//            } catch (JSONException e) {
//                throw new HandlerException("Malformed response for " + request.getRequestLine(), e);
            } finally {
                if (input != null) input.close();
            }
        } catch (HandlerException e) {
            throw e;
        } catch (IOException e) {
            throw new HandlerException("Problem reading remote response for "
                    + request.getRequestLine(), e);
        }
    }

}
