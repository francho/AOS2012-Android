package org.agilespain.kitaos.widget;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.widget.SimpleCursorTreeAdapter;
import org.agilespain.kitaos.provider.KitaosContract;

public class TalksByHourTreeAdapter extends SimpleCursorTreeAdapter {

    private final ContentResolver mContentResover;

    // Note that the constructor does not take a Cursor. This is done to avoid querying the
    // database on the main thread.
    public TalksByHourTreeAdapter(Context context, Cursor cursor,
                                  String[] groupFrom, int[] groupTo,
                                  String[] childrenFrom, int[] childrenTo) {

        super(context, cursor,
                org.agilespain.kitaos.R.layout.expandable_list_group_title, groupFrom, groupTo,
                org.agilespain.kitaos.R.layout.expandable_list_item_talk, childrenFrom, childrenTo);

        mContentResover = context.getContentResolver();
    }

    @Override
    protected Cursor getChildrenCursor(Cursor groupCursor) {
        String[] projection = new String[]{
                KitaosContract.Talks._ID,
                KitaosContract.Talks.TITLE,
                KitaosContract.Talks.ROOM,
                KitaosContract.Talks.SPEAKER
        };

        if (groupCursor == null || mContentResover == null) {
            return new MatrixCursor(projection);
        }

        Long startDate = groupCursor.getLong(0);

        return mContentResover.query(KitaosContract.Talks.uri(),
                projection,
                KitaosContract.Talks.START_DATE + "=?",
                new String[]{"" + startDate},
                KitaosContract.Talks.ROOM + " ASC");

    }


}