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

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;
import org.agilespain.kitaos.provider.KitaosContract;
import org.agilespain.kitaos.widget.AvatarView;

public class TalkActivity extends KitaosBaseActivity implements View.OnClickListener {
    private static final String TAG_PANEL = "panel";
    private static final String TAG_TALKS = "talks";
    private TextView mTitle;
    private TextView mDescription;
    private TextView mRoom;
    private TextView mSpeaker;
    private TextView mTimeStart;
    private TextView mSpeakerEmail;
    private TextView mSpeakerTwitter;
    private AvatarView mSpeakerAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.talk);

        mTitle = (TextView) findViewById(R.id.talk_title);
        mDescription = (TextView) findViewById(R.id.talk_description);
        mRoom = (TextView) findViewById(R.id.talk_room);
        mSpeaker = (TextView) findViewById(R.id.talk_speaker);
        mTimeStart = (TextView) findViewById(R.id.talk_time_start);
        mSpeakerEmail = (TextView) findViewById(R.id.talk_speaker_email);
        mSpeakerTwitter = (TextView) findViewById(R.id.talk_speaker_twitter);
        mSpeakerAvatar = (AvatarView) findViewById(R.id.talk_speaker_avatar);
    }

    @Override
    protected void onStart() {
        super.onStart();

        loadTalk(getIntent().getData());
    }

    private void loadTalk(Uri uri) {
        String[] projection = new String[]{
                KitaosContract.Talks.TITLE,
                KitaosContract.Talks.ROOM,
                KitaosContract.Talks.SPEAKER,
                KitaosContract.Talks.DESCRIPTION,
                KitaosContract.Talks.START_DATE,
                KitaosContract.Talks.SPEAKER_EMAIL,
                KitaosContract.Talks.SPEAKER_TWITTER
        };

        Cursor talkInfo = getContentResolver().query(uri, projection, null, null, null);
        if (talkInfo.moveToFirst()) {
            mTitle.setText(talkInfo.getString(0));
            mRoom.setText(talkInfo.getString(1));
            mSpeaker.setText(talkInfo.getString(2));
            mDescription.setText(talkInfo.getString(3));

            CharSequence time = DateFormat.format("h:mm aa", talkInfo.getLong(4));
            mTimeStart.setText(time);

            String email = talkInfo.getString(5);
            mSpeakerEmail.setText(email);
            mSpeakerEmail.setOnClickListener(this);

            mSpeakerAvatar.setAvatar(email);

            String twitter = talkInfo.getString(6);
            if (!twitter.startsWith("@")) {
                twitter = "@" + twitter;
            }
            mSpeakerTwitter.setText(twitter);
            mSpeakerTwitter.setOnClickListener(this);
        }
        talkInfo.close();

    }


    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.talk_speaker_email:
                String email = "" + ((TextView) view).getText();
                intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                break;
            case R.id.talk_speaker_twitter:
                String twitter = (String) ((TextView) view).getText();
                twitter.replaceFirst("@", "");
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/#!/" + twitter));
                break;
            default:
                return;
        }

        if (intent != null) {
            startActivity(intent);
        }
    }
}
