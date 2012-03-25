package org.agilespain.kitaos;

import android.content.Intent;
import android.os.*;
import android.widget.ProgressBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import org.agilespain.kitaos.app.KitaosIntent;
import org.agilespain.kitaos.service.SyncService;

/**
 * Created by IntelliJ IDEA.
 * User: francho
 * Date: 25/03/12
 * Time: 00:23
 * To change this template use File | Settings | File Templates.
 */
public class KitaosBaseActivity extends SherlockFragmentActivity {

    private MenuItem mItemReload = null;

    class SyncResultReceiver extends ResultReceiver {
        public SyncResultReceiver() {
            super(new Handler());
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            switch (resultCode) {
                case SyncService.STATUS_RUNNING:
                    // setSupportProgressBarIndeterminateVisibility(true);
                    if(mItemReload!=null) mItemReload.setActionView(R.layout.progressbar);
                    break;
                case SyncService.STATUS_ERROR:
                case SyncService.STATUS_FINISHED:
                    if(mItemReload!=null) mItemReload.setActionView(null);
                    // setSupportProgressBarIndeterminateVisibility(false);
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getSupportMenuInflater().inflate(R.menu.main_menu, menu);
        
        mItemReload = menu.findItem(R.id.menu_item_reload);
        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_item_reload:
                syncData();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void syncData() {
        Intent intent = new Intent(KitaosIntent.ACTION_SYNC);
        ResultReceiver receiver = new SyncResultReceiver();
        intent.putExtra(SyncService.EXTRA_STATUS_RECEIVER, (Parcelable) receiver);
        startService(intent);
    }
}
