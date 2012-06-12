package org.agilespain.kitaos;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.test.AndroidTestCase;
import android.test.ProviderTestCase2;
import android.text.format.DateFormat;
import org.agilespain.kitaos.provider.KitaosContract;
import org.agilespain.kitaos.service.TalksJsonHandler;
import org.agilespain.kitaos.provider.KitaosContract.Talks;
import org.agilespain.kitaos.provider.KitaosProvider;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Test the json handler for the talks service
 *
 * @author francho
 */
public class TalksJsonHandlerTest extends ProviderTestCase2<KitaosProvider> {
    // private KitaosProvider mProvider;
    private TalksJsonHandler mTalksHandler;


    public TalksJsonHandlerTest() {
        super(KitaosProvider.class, KitaosContract.CONTENT_AUTHORITY);
    }

    public void setUp() throws Exception {
        super.setUp();
        mTalksHandler = new TalksJsonHandler();
    }

    /**
     * Exposes method {@code getTestContext()} in {@link AndroidTestCase}, which
     * is hidden for now. Useful for obtaining access to the test assets.
     */
    public static Context getTestContext(AndroidTestCase testCase) throws IllegalAccessException,
            InvocationTargetException, NoSuchMethodException {
        return (Context) AndroidTestCase.class.getMethod("getTestContext").invoke(testCase);
    }



    private String getDummyJson() {
        InputStream mockTalks=null;
        String json = "";
        try {
            mockTalks = getTestContext(this).getAssets().open("talks.json");

            int size = mockTalks.available();
            byte[] buffer = new byte[size];
            mockTalks.read(buffer);
            mockTalks.close();

            // byte buffer into a string
            json = new String(buffer);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                mockTalks.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        return json;
    }

    public void testParseTalksJson() throws Exception {
        ArrayList<ContentProviderOperation> batch = mTalksHandler.parse(getDummyJson(), getContext().getContentResolver());

        assertEquals(4, batch.size());

        getProvider().applyBatch(batch);

        Cursor cursor = getProvider().query(Talks.uri(24001), null, null, null, null);
        cursor.moveToFirst();

        assertEquals("Android", cursor.getString(cursor.getColumnIndex(Talks.TITLE)));
        long datetimestamp = cursor.getLong(cursor.getColumnIndex(Talks.START_DATE));
        CharSequence date = DateFormat.format("yyyy-MM-dd h:mm", new Date(datetimestamp));
        assertEquals("2012-06-23 9:30", date);

        int duration = (int) ((cursor.getLong(cursor.getColumnIndex(Talks.END_DATE)) - datetimestamp) / (60 * 60 * 1000));

        assertEquals(1, duration);
        assertEquals("sala3", cursor.getString(cursor.getColumnIndex(Talks.ROOM)));

        assertEquals("bill@microsoft.com", cursor.getString(cursor.getColumnIndex(Talks.SPEAKER_EMAIL)));
        assertEquals("fbgblog", cursor.getString(cursor.getColumnIndex(Talks.SPEAKER_TWITTER)));
        assertEquals("Bill Gates", cursor.getString(cursor.getColumnIndex(Talks.SPEAKER)));
        assertEquals("¿Cuanto nos paga?", cursor.getString(cursor.getColumnIndex(Talks.DESCRIPTION)));

    }

    public void testGetMillis() throws Exception {
        String expected = "2012-02-29 10:30";

        long millis = mTalksHandler.getMillis(expected);
        Date date = new Date(millis);

        CharSequence actual = DateFormat.format("yyyy-MM-dd h:mm", date);
        assertEquals(expected, actual);
    }
}