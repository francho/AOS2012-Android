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

package org.agilespain.kitaos.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.LayerDrawable;
import android.view.Gravity;
import android.widget.Button;
import org.agilespain.kitaos.R;

/**
 * Custom view that represents a {@link com.google.android.apps.iosched.provider.ScheduleContract.Blocks#BLOCK_ID} instance, including its
 * title and time span that it occupies. Usually organized automatically by
 * {@link com.google.android.apps.iosched.ui.widget.BlocksLayout} to match up against a {@link com.google.android.apps.iosched.ui.widget.TimeRulerView} instance.
 */
public class PostitView extends com.google.android.apps.iosched.ui.widget.BlockView {
    public PostitView(Context context, String blockId, String title, long startTime,
                      long endTime, boolean containsStarred, int column) {

        super(context, blockId, title, startTime, endTime, containsStarred, column);
        Typeface font = Typeface.createFromAsset(context.getAssets(), "ShadowsIntoLightTwo-Regular.ttf");
        setTypeface(font);
        
        setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);

        setMaxLines(4);
    }
}
