package org.agilespain.kitaos.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;
import org.agilespain.kitaos.R;
import org.agilespain.kitaos.provider.KitaosContract;
import org.agilespain.kitaos.widget.TalksByHourTreeAdapter;
import org.agilespain.kitaos.widget.TypefaceUtils;

/**
 * @author francho (http://francho.org)
 */
public class FragmentTalks extends android.support.v4.app.Fragment implements TalksByHourTreeAdapter.ViewBinder, ExpandableListView.OnChildClickListener {

    private TalksByHourTreeAdapter mAdapter = null;
    private ExpandableListView mTalksList;

    /**
     * onCreate
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Typeface mTitleFont = TypefaceUtils.getTitleFont(this.getActivity());
        Typeface mNormalFont = TypefaceUtils.getNormalFont(this.getActivity());
    }

    /**
     * onDestroy
     */
    @Override
    public void onDestroy() {
        super.onDestroy();

        // Null out the group cursor. This will cause the group cursor and all of the child cursors
        // to be closed.
        if (mAdapter != null) {
            mAdapter.changeCursor(null);
            mAdapter = null;
        }
    }

    /**
     * Attach to list view once Fragment is ready to run.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mTalksList == null) {
            return;
        }
        if (mAdapter == null) {
            mAdapter = getTalksAdapter();
        }
        mTalksList.setAdapter(mAdapter);
        mTalksList.setOnChildClickListener(this);
    }

    /**
     * onCreate
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mTalksList = (ExpandableListView) inflater.inflate(R.layout.expandable_list, container, false);

        return mTalksList;
    }

    /**
     * getApdater
     *
     * @return
     */
    private TalksByHourTreeAdapter getTalksAdapter() {
        Context context = getActivity();

        Cursor cursor = context.getContentResolver().query(KitaosContract.Talks.hoursUri(),
                new String[]{KitaosContract.Talks.START_DATE, KitaosContract.Talks._ID},
                null,
                null,
                KitaosContract.Talks.START_DATE + " ASC");

        TalksByHourTreeAdapter adapter = new TalksByHourTreeAdapter(context,
                cursor,
                new String[]{KitaosContract.Talks.START_DATE}, // Name for group layouts
                new int[]{android.R.id.text1},
                new String[]{
                        KitaosContract.Talks.TITLE,
                        KitaosContract.Talks.ROOM,
                        KitaosContract.Talks.SPEAKER
                }, // Number for child layouts
                new int[]{
                        R.id.talk_title,
                        R.id.talk_room,
                        R.id.talk_speaker
                });

        adapter.setViewBinder(this);

        return adapter;
    }

    /**
     * setViewValue
     *
     * @param view
     * @param cursor
     * @param i
     * @return
     */
    @Override
    public boolean setViewValue(View view, Cursor cursor, int i) {
        if (view.getId() == android.R.id.text1) {
            CharSequence time = DateFormat.format("h:mm aa", cursor.getLong(i));
            ((TextView) view).setText(time);
            return true;
        }

        return false;
    }


    @Override
    public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
        Uri uri = KitaosContract.Talks.uri(l);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);

        startActivity(intent);
        return true;
    }
}
