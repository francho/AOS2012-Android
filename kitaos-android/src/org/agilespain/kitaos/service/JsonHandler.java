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

import org.json.JSONException;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.os.RemoteException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Abstract class that handles reading and parsing an {@link org.xmlpull.v1.XmlPullParser} into
 * a set of {@link android.content.ContentProviderOperation}. It catches recoverable network
 * exceptions and rethrows them as {@link org.agilespain.kitaos.service.HandlerException}. Any local
 * {@link android.content.ContentProvider} exceptions are considered unrecoverable.
 * <p>
 * This class is only designed to handle simple one-way synchronization.
 */
public abstract class JsonHandler {
    private final String mAuthority;

    JsonHandler() {
        mAuthority = org.agilespain.kitaos.provider.KitaosContract.CONTENT_AUTHORITY;
    }

    /**
     * Parse the given {@link org.json}, turning into a series of
     * {@link android.content.ContentProviderOperation} that are immediately applied using the
     * given {@link android.content.ContentResolver}.
     *
     * @param jsonString
     * @param resolver
     * @throws HandlerException
     */
    public void parseAndApply(String jsonString, ContentResolver resolver)
            throws HandlerException {
        try {
            final ArrayList<ContentProviderOperation> batch = parse(jsonString, resolver);
            resolver.applyBatch(mAuthority, batch);

        } catch (HandlerException e) {
            throw e;
        } catch (JSONException e) {
        	e.printStackTrace();
            throw new HandlerException("Problem parsing JSON response", e);
        } catch (IOException e) {
            throw new HandlerException("Problem reading response", e);
        } catch (RemoteException e) {
            // Failed binder transactions aren't recoverable
            throw new RuntimeException("Problem applying batch operation", e);
        } catch (OperationApplicationException e) {
            // Failures like constraint violation aren't recoverable
            // TODO: write unit tests to exercise full provider
            // TODO: consider catching version checking asserts here, and then
            // wrapping around to retry parsing again.
            throw new RuntimeException("Problem applying batch operation", e);
        }
    }

    /**
     * Parse the given {@link org.json}, returning a set of
     * {@link android.content.ContentProviderOperation} that will bring the
     * {@link android.content.ContentProvider} into sync with the parsed data.
     */
    public abstract ArrayList<ContentProviderOperation> parse(String jsonString,
            ContentResolver resolver) throws JSONException, IOException;


}
