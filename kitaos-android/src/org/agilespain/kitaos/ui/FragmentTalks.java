package org.agilespain.kitaos.ui;

import android.content.AsyncQueryHandler;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorTreeAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;
import org.agilespain.kitaos.R;
import org.agilespain.kitaos.provider.KitaosContract;
import org.agilespain.kitaos.widget.TypefaceUtils;

/**
 * Created by IntelliJ IDEA.
 * User: francho
 * Date: 23/03/12
 * Time: 18:51
 * To change this template use File | Settings | File Templates.
 */
public class FragmentTalks extends android.support.v4.app.Fragment implements SimpleCursorTreeAdapter.ViewBinder {

    private SimpleCursorTreeAdapter mAdapter;
    private QueryHandler mQueryHandler;
    private Typeface mTitleFont;
    private Typeface mNormalFont;


    /**
     * Async querys
     */
    private static final class QueryHandler extends AsyncQueryHandler {
        private CursorTreeAdapter mAdapter;

        public static final int TOKEN_GROUP = 0;
        public static final int TOKEN_CHILD = 1;

        public QueryHandler(Context context, CursorTreeAdapter adapter) {
            super(context.getContentResolver());
            this.mAdapter = adapter;
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            switch (token) {
                case TOKEN_GROUP:
                    mAdapter.setGroupCursor(cursor);
                    break;

                case TOKEN_CHILD:
                    int groupPosition = (Integer) cookie;
                    mAdapter.setChildrenCursor(groupPosition, cursor);
                    break;
            }
        }
    }

    /**
     * List adapter
     */
    public class MyExpandableListAdapter extends SimpleCursorTreeAdapter {

        // Note that the constructor does not take a Cursor. This is done to avoid querying the 
        // database on the main thread.
        public MyExpandableListAdapter(Context context,
                                       int groupLayout, String[] groupFrom, int[] groupTo,
                                       int childLayout, String[] childrenFrom, int[] childrenTo) {

            super(context, null,
                    groupLayout, groupFrom, groupTo,
                    childLayout, childrenFrom, childrenTo);
        }

        @Override
        protected Cursor getChildrenCursor(Cursor groupCursor) {
            // Given the group, we return a cursor for all the children within that group 
            Long startDate = groupCursor.getLong(0);
            mQueryHandler.startQuery(QueryHandler.TOKEN_CHILD,
                    groupCursor.getPosition(),
                    KitaosContract.Talks.uri(),
                    new String[]{
                            KitaosContract.Talks._ID,
                            KitaosContract.Talks.TITLE,
                            KitaosContract.Talks.ROOM,
                            KitaosContract.Talks.SPEAKER
                    },
                    KitaosContract.Talks.START_DATE + "=?",
                    new String[]{""+startDate},
                    KitaosContract.Talks.ROOM + " ASC");

            return null;
        }
    }

    /**
     * onCreate
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = getAdapter();
        mQueryHandler = new QueryHandler(this.getActivity(), mAdapter);

        mTitleFont = TypefaceUtils.getTitleFont(this.getActivity());

        mNormalFont = TypefaceUtils.getNormalFont(this.getActivity());
    }

    /**
     * onDestroy
     */
    @Override
    public void onDestroy() {
        super.onDestroy();

        // Null out the group cursor. This will cause the group cursor and all of the child cursors
        // to be closed.
        mAdapter.changeCursor(null);
        mAdapter = null;
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
        ExpandableListView v = (ExpandableListView) inflater.inflate(R.layout.expandable_list, container, false);


        // Query for talks
        mQueryHandler.startQuery(QueryHandler.TOKEN_GROUP,
                null,
                KitaosContract.Talks.hoursUri(),
                null,
                null,
                null,
                KitaosContract.Talks.START_DATE + " ASC");
        v.setAdapter(mAdapter);
        return v;
    }

    /**
     * getApdater
     *
     * @return
     */
    private SimpleCursorTreeAdapter getAdapter() {
        SimpleCursorTreeAdapter adapter = new MyExpandableListAdapter(this.getActivity(),
                R.layout.expandable_list_group_title,
                new String[]{KitaosContract.Talks.START_DATE}, // Name for group layouts
                new int[]{android.R.id.text1},
                R.layout.expandable_list_item_talk,
                new String[]{
                        KitaosContract.Talks.TITLE, 
                        KitaosContract.Talks.ROOM
                }, // Number for child layouts
                new int[]{
                        R.id.talk_title,
                        R.id.talk_room
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
}
