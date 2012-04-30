package org.agilespain.kitaos.widget;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.widget.SimpleCursorTreeAdapter;
import org.agilespain.kitaos.provider.KitaosContract;

public class TalksByHourTreeAdapter extends SimpleCursorTreeAdapter {

    private ContentResolver mContentResover;

    // Note that the constructor does not take a Cursor. This is done to avoid querying the
    // database on the main thread.
    public TalksByHourTreeAdapter(Context context, Cursor cursor,
                                  int groupLayout, String[] groupFrom, int[] groupTo,
                                  int childLayout, String[] childrenFrom, int[] childrenTo) {

        super(context, cursor,
                groupLayout, groupFrom, groupTo,
                childLayout, childrenFrom, childrenTo);

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