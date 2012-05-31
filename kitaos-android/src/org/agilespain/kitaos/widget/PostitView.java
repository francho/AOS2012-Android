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
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import org.agilespain.kitaos.R;
import org.agilespain.kitaos.provider.KitaosContract;

import java.io.IOException;
import java.util.Random;

/**
 * Custom view that represents a {@link com.google.android.apps.iosched.provider.ScheduleContract.Blocks#BLOCK_ID} instance, including its
 * title and time span that it occupies. Usually organized automatically by
 * {@link com.google.android.apps.iosched.ui.widget.BlocksLayout} to match up against a {@ link com.google.android.apps.iosched.ui.widget.TimeRulerView} instance.
 */
public class PostitView extends com.google.android.apps.iosched.ui.widget.BlockView implements View.OnClickListener {

    private float rotation = 3;

    private long _id = -1;

    public PostitView(Context context, long blockId, String title, long startTime,
                      long endTime, boolean containsStarred, int column) {

        super(context, ""+blockId, title, startTime, endTime, containsStarred, column);

        setPostitBackground(containsStarred);
        setTextColor(Color.BLACK);
        setPostitTypeface();

        Random random = new Random();

        rotation = random.nextInt(5) * (random.nextBoolean()?-1:1);
        
        setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL | Gravity.FILL_VERTICAL);

        setMaxLines(2);
        setEllipsize(TextUtils.TruncateAt.END);

        this._id = blockId;
        setOnClickListener(this);
    }

    @Override
    protected void onDraw(Canvas c) {
        c.rotate(rotation);
        super.onDraw(c);
    }

    private void setPostitTypeface() {
        setTypeface(TypefaceUtils.getNormalFont(getContext()));
    }

    private void setPostitBackground(boolean containsStarred) {
        LayerDrawable bg = (LayerDrawable)
                getContext().getResources().getDrawable(R.drawable.btn_block);
        int accentColor = (containsStarred) ? Color.rgb(255, 122, 144) : Color.YELLOW;
        bg.getDrawable(0).setColorFilter(accentColor, PorterDuff.Mode.SRC_ATOP);
        bg.getDrawable(1).setAlpha(255);

        setBackgroundDrawable(bg);
    }


    @Override
    public void onClick(View view) {
        Uri uri = KitaosContract.Talks.uri(_id);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);

        getContext().startActivity(intent);
    }
}
