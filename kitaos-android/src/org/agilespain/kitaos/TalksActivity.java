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
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ResultReceiver;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.TabHost;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import org.agilespain.kitaos.app.KitaosIntent;
import org.agilespain.kitaos.service.SyncService;
import org.agilespain.kitaos.ui.FragmentPanel;
import org.agilespain.kitaos.ui.FragmentTalks;
import org.agilespain.kitaos.widget.ViewPagerTabsAdapter;

public class TalksActivity extends KitaosBaseActivity {
    private static final String TAG_PANEL = "panel";
    private static final String TAG_TALKS = "talks";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabhost);
        setSupportProgressBarIndeterminateVisibility(false);

        setupTabs();
    }



    private void setupTabs() {
        TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
        tabHost.setup();
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        ViewPagerTabsAdapter tabsAdapter = new ViewPagerTabsAdapter(this, tabHost, viewPager);
        final TabHost.TabSpec tab1 = createTabSpec(tabHost, TAG_PANEL,
                R.string.tab_panel);
        tabsAdapter.addTab(tab1, FragmentPanel.class, null);
        final TabHost.TabSpec tab2 = createTabSpec(tabHost, TAG_TALKS,
                R.string.tab_talks);
        tabsAdapter.addTab(tab2, FragmentTalks.class, null);
    }

    private TabHost.TabSpec createTabSpec(TabHost tabHost, String tag, int labelId) {
        final Context context = tabHost.getContext();

        final TabHost.TabSpec tabSpec = tabHost.newTabSpec(tag);

        final CharSequence label = context.getText(labelId);

        AttributeSet attrs = null;
        int defStyle = R.attr.kitaosTabViewStyle;
        final TextView view = new TextView(context, attrs, defStyle);
        view.setText(label);

        tabSpec.setIndicator(view);

        return tabSpec;
    }
}
