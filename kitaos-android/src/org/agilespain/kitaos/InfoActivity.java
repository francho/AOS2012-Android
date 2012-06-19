/*
 * Copyright (C) 2011 Jake Wharton
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
package org.agilespain.kitaos;


import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.actionbarsherlock.view.MenuItem;

public class InfoActivity extends KitaosBaseActivity {
    private WebView mWebview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);
        setSupportProgressBarIndeterminateVisibility(false);

        getSupportActionBar().setHomeButtonEnabled(true);

        initWebview();
    }

    private void initWebview() {
        mWebview = (WebView) findViewById(R.id.webview);
        mWebview.setWebViewClient(new KitaosWebViewClient());
        final WebSettings settings = mWebview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setCacheMode(WebSettings.LOAD_NORMAL);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Uri url = getIntent().getData();
        mWebview.loadUrl(url.toString());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_reload:
                mWebview.reload();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private class KitaosWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if (url.contains(getString(R.string.url_info))) {
                view.loadUrl(url);
                return true;
            }

            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            setKitaosProgressbarVisible(true);
            super.onPageStarted(view, url, favicon);

        }


        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            setKitaosProgressbarVisible(false);
        }
    }
}
