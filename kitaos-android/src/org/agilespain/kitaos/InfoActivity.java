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


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.widget.TabHost;
import android.widget.TextView;
import org.agilespain.kitaos.ui.FragmentPanel;
import org.agilespain.kitaos.ui.FragmentTalks;
import org.agilespain.kitaos.widget.ViewPagerTabsAdapter;

public class InfoActivity extends KitaosBaseActivity {
    private WebView mWebview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);
        setSupportProgressBarIndeterminateVisibility(false);

        initWebview();
    }

    private void initWebview() {
        mWebview = (WebView) findViewById(R.id.webview);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Uri url = getIntent().getData();
        mWebview.loadUrl(url.toString());
    }

}
