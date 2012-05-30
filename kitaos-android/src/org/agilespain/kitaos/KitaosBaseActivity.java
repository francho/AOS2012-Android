package org.agilespain.kitaos;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.*;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import org.agilespain.kitaos.app.KitaosIntent;
import org.agilespain.kitaos.service.SyncService;

/**
 * @author francho
 * @see http://francho.org
 *
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
                    setKitaosProgressbarVisible(true);
                    break;
                case SyncService.STATUS_ERROR:
                case SyncService.STATUS_FINISHED:
                    setKitaosProgressbarVisible(false);
                    break;
            }
        }
    }

    protected void setKitaosProgressbarVisible(boolean visible) {
        if(mItemReload==null) {
            return;
        }

        if (visible) {
            mItemReload.setActionView(R.layout.progressbar);
        } else {
            mItemReload.setActionView(null);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        syncData();
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
            case android.R.id.home:
                goHome();
                return true;
            case R.id.menu_item_reload:
                syncData();
                return true;
            case R.id.menu_item_info:
                showInfo();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void goHome() {
        PackageManager pm = getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(getApplicationInfo().packageName);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    protected void syncData() {
        Intent intent = new Intent(KitaosIntent.ACTION_SYNC);
        ResultReceiver receiver = new SyncResultReceiver();
        intent.putExtra(SyncService.EXTRA_STATUS_RECEIVER, (Parcelable) receiver);
        startService(intent);
    }

    protected void showInfo() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setPackage(getApplicationInfo().packageName);
        String url = getString(R.string.url_info);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

}
