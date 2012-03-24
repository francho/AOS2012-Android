package org.agilespain.kitaos.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import com.google.android.apps.iosched.provider.ScheduleContract;
import com.google.android.apps.iosched.ui.widget.BlocksLayout;
import com.google.android.apps.iosched.util.ParserUtils;
import org.agilespain.kitaos.R;
import org.agilespain.kitaos.provider.KitaosContract;
import org.agilespain.kitaos.widget.PostitView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: francho
 * Date: 23/03/12
 * Time: 18:51
 * To change this template use File | Settings | File Templates.
 */
public class FragmentPanel extends android.support.v4.app.Fragment {

    private static final String TAG = "BlocksActivity";

    // TODO: these layouts and views are structured pretty weird, ask someone to
    // review them and come up with better organization.

    // TODO: show blocks that don't fall into columns at the bottom

    public static final String EXTRA_TIME_START = "com.google.android.iosched.extra.TIME_START";
    public static final String EXTRA_TIME_END = "com.google.android.iosched.extra.TIME_END";

    private ScrollView mScrollView;
    private BlocksLayout mBlocks;
    private View mNowView;

    private long mTimeStart = -1;
    private long mTimeEnd = -1;

    private static final int DISABLED_BLOCK_ALPHA = 255;
    private ContentObserver mObserver = new BlocksContentObserver();

    class BlocksContentObserver extends  ContentObserver {
        public BlocksContentObserver() {
            super(null);
        }

        @Override
        public void onChange(boolean selfChange) {
            mHandler.removeMessages(MSG_UPDATE);
            mHandler.sendEmptyMessageDelayed(MSG_UPDATE, 300);
        }
    }

    public static final int MSG_UPDATE = 99;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what== MSG_UPDATE) {
                updateTalks();
            }
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.blocks_content, container, false);

        mTimeStart = getActivity().getIntent().getLongExtra(EXTRA_TIME_START, mTimeStart);
        mTimeEnd = getActivity().getIntent().getLongExtra(EXTRA_TIME_END, mTimeEnd);

        mScrollView = (ScrollView) v.findViewById(R.id.blocks_scroll);
        mBlocks = (BlocksLayout) v.findViewById(R.id.blocks);
        mNowView = v.findViewById(R.id.blocks_now);

        mBlocks.setDrawingCacheEnabled(true);
        mBlocks.setAlwaysDrawnWithCacheEnabled(true);

        getActivity().getContentResolver().registerContentObserver(KitaosContract.Talks.uri(), true, mObserver);

        updateTalks();

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNowView.post(new Runnable() {
            public void run() {
                updateNowView(true);
            }
        });
    }

// private static final HashMap<String, Integer> sTypeColumnMap = buildTypeColumnMap();

    private static HashMap<String, Integer> buildTypeColumnMap() {
        final HashMap<String, Integer> map = new HashMap<String, Integer>();
        map.put(ParserUtils.BLOCK_TYPE_FOOD, 0);
        map.put(ParserUtils.BLOCK_TYPE_SESSION, 1);
        map.put(ParserUtils.BLOCK_TYPE_OFFICE_HOURS, 2);
        return map;
    }

    protected void updateTalks() {
        mBlocks.removeAllBlocks();

        if(!this.isAdded()) {
            mHandler.removeMessages(MSG_UPDATE);
            mHandler.sendEmptyMessageDelayed(MSG_UPDATE, 300);
            return;
        }

        String[] projection = {
                KitaosContract.Talks._ID,
                KitaosContract.Talks.TITLE,
                KitaosContract.Talks.START_DATE,
                KitaosContract.Talks.END_DATE,
                KitaosContract.Talks.ROOM
        };
        Cursor cursor = getActivity().getContentResolver().query(KitaosContract.Talks.uri(), projection, null, null, null);
        try {
            ArrayList<String> salas = new ArrayList<String>();
            while (cursor.moveToNext()) {
                String blockId = cursor.getString(0);
                String title = cursor.getString(1);
                long start = cursor.getLong(2);
                long end = cursor.getLong(3);
                String sala = cursor.getString(4);

                int column = salas.indexOf(sala);

                Log.d("BLOCKS", salas.toString());
                Log.d("BLOCKS", "sala:"+sala+" column:" + column + " start:" + DateFormat.format("yyyy-MM-dd h:mm", new Date(start)) + " end:" + end + " " + title);
                if(column < 0) {
                    salas.add(sala);
                    column = salas.indexOf(sala);
                }

                boolean containsStarred=Math.random() * 10 < 1;

                Log.d("BLOCKS", "column:" + column + " start:" + start + " end:" + end + " " + title);

                final PostitView postit = new PostitView(getActivity(), blockId, title, start, end, containsStarred, column);
                mBlocks.addBlock(postit);
            }
        } finally {
            cursor.close();
        }

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Since we build our views manually instead of using an adapter, we
        // need to manually requery every time launched.
        //final Uri blocksUri = getIntent().getData();
//        mHandler.startQuery(blocksUri, BlocksQuery.PROJECTION, Blocks.DEFAULT_SORT);

        // Start listening for time updates to adjust "now" bar. TIME_TICK is
        // triggered once per minute, which is how we move the bar over time.
        final IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        activity.registerReceiver(mReceiver, filter, null, new Handler());

        // onQueryComplete(0,null,null);

    }

    @Override
    public void onDetach() {
        super.onDetach();    //To change body of overridden methods use File | Settings | File Templates.

        super.onPause();
        getActivity().unregisterReceiver(mReceiver);
    }
    /**
     * {@inheritDoc}
     */
    public void onClick(View view) {
        if (view instanceof PostitView) {
            final String blockId = ((PostitView) view).getBlockId();
            final Uri sessionsUri = ScheduleContract.Blocks.buildSessionsUri(blockId);
            startActivity(new Intent(Intent.ACTION_VIEW, sessionsUri));
        }
    }

    /**
     * Update position and visibility of "now" view.
     */
    private void updateNowView(boolean forceScroll) {
        final long now = System.currentTimeMillis();

        final boolean visible = now >= mTimeStart && now <= mTimeEnd;
        mNowView.setVisibility(visible ? View.VISIBLE : View.GONE);

        if (visible && forceScroll) {
            // Scroll to show "now" in center
            final int offset = mScrollView.getHeight() / 2;
            mNowView.requestRectangleOnScreen(new Rect(0, offset, 0, offset), true);
        }

        mBlocks.requestLayout();
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive time update");
            updateNowView(false);
        }
    };

}
