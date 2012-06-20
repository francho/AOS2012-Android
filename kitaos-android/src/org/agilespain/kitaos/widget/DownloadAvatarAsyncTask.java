package org.agilespain.kitaos.widget;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import org.agilespain.kitaos.R;
import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class DownloadAvatarAsyncTask extends AsyncTask<String, Void, String> {
    private final Context mContext;
    private ContentResolver mContentResolver;

    /**
     * Constructor
     *
     * @param context
     */
    public DownloadAvatarAsyncTask(Context context) {
        mContext = context;
    }


    /*
    * (non-Javadoc)
    *
    * @see android.os.AsyncTask#doInBackground(java.lang.Object)
    */
    @Override
    protected String doInBackground(String... emailArgv) {
        Bitmap bm = null;

        try {
            String email = emailArgv[0];

            File file = new File(mContext.getApplicationContext().getCacheDir(), email);

            URL url = getAvatarUrl(email);

            /* Open a connection to that URL. */
            URLConnection ucon = url.openConnection();

            /*
            * Define InputStreams to read from the URLConnection.
            */
            InputStream is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

            /*
            * Read bytes to the Buffer until there is nothing more to
            * read(-1).
            */
            ByteArrayBuffer baf = new ByteArrayBuffer(50);
            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }

            /* Convert the Bytes read to a String. */
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baf.toByteArray());
            fos.close();

            return (mContext.getApplicationContext().getCacheDir() + email);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private URL getAvatarUrl(String email) throws MalformedURLException {
        String urlFormatter = mContext.getResources().getString(R.string.url_avatar);

        String url = String.format(urlFormatter, email);

        Log.d("avatar url", url);
        return new URL(url);
    }

}