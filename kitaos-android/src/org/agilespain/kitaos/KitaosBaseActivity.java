package org.agilespain.kitaos;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import org.agilespain.kitaos.app.KitaosIntent;
import org.agilespain.kitaos.service.SyncService;

/**
 *
 *
 */
class KitaosBaseActivity extends SherlockFragmentActivity {

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

    void setKitaosProgressbarVisible(boolean visible) {
        if (mItemReload == null) {
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
        syncData(false);
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
        switch (item.getItemId()) {
            case android.R.id.home:
                goHome();
                return true;
            case R.id.menu_item_reload:
                syncData(true);
                return true;
            case R.id.menu_item_info:
                showInfo();
                return true;
            case R.id.menu_item_map:
                showMap();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void goHome() {
        PackageManager pm = getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(getApplicationInfo().packageName);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    void syncData(boolean force) {
        Intent intent = new Intent(KitaosIntent.ACTION_SYNC);
        ResultReceiver receiver = new SyncResultReceiver();
        intent.putExtra(SyncService.EXTRA_STATUS_RECEIVER, receiver);
        intent.putExtra(SyncService.EXTRA_FORCE_RELOAD, force);
        startService(intent);
    }

    void showInfo() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setPackage(getApplicationInfo().packageName);
        String url = getString(R.string.url_info);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    void showMap() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String url = "http://kit-aos.appspot.com/static/img/planoAOS2012.png";
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
}
