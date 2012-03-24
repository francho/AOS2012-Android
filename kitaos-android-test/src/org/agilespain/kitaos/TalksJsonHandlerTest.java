package org.agilespain.kitaos;

import android.content.ContentProviderOperation;
import android.database.Cursor;
import android.test.ProviderTestCase2;
import android.text.format.DateFormat;
import org.agilespain.kitaos.provider.KitaosContract;
import org.agilespain.kitaos.service.TalksJsonHandler;
import org.agilespain.kitaos.provider.KitaosContract.Talks;
import org.agilespain.kitaos.provider.KitaosProvider;

import java.util.ArrayList;
import java.util.Date;

/**
 * Test the json handler for the talks service
 * @author francho
 */
public class TalksJsonHandlerTest  extends ProviderTestCase2<KitaosProvider> {
    // private KitaosProvider mProvider;
    private TalksJsonHandler mTalksHandler;

    public TalksJsonHandlerTest(){
        super(KitaosProvider.class, KitaosContract.CONTENT_AUTHORITY);
    }

    public void setUp() throws Exception {
        super.setUp();
        mTalksHandler = new TalksJsonHandler();
    }
    
    private String getDummyJson() {
        String json = "";
        json += "{ \"2\" : { \"date\" : \"2012-06-23\",  ";
        json += "        \"duration\" : 1, ";
        json += "        \"hour\" : 11,   ";
        json += "        \"room\" : { \"name\" : \"sala1\" },";
        json += "    \"time\" : \"11:0\",   ";
        json += "            \"title\" : \"Android\" ";
        json += "},                         ";
        json += "    \"4001\" : { \"date\" : \"2012-06-23\", ";
        json += "        \"duration\" : 1,          ";
        json += "        \"hour\" : 9,      ";
        json += "        \"room\" : { \"name\" : \"sala2\" }, ";
        json += "    \"time\" : \"9:0\",             ";
        json += "            \"title\" : \"Kanban\"  ";
        json += "}                          ";
        json += "}                        ";

        return json;
    }
    
    public void testParseTalksJson() throws Exception {
        ArrayList<ContentProviderOperation> batch = mTalksHandler.parse(getDummyJson(), getContext().getContentResolver());

        assertEquals(2, batch.size());

        getProvider().applyBatch(batch);

        Cursor cursor = getProvider().query(Talks.uri(2), null, null, null, null);
        cursor.moveToFirst();

        assertEquals("Android", cursor.getString(cursor.getColumnIndex(Talks.TITLE)));
        long datetimestamp = cursor.getLong(cursor.getColumnIndex(Talks.START_DATE));
        CharSequence date =  DateFormat.format("yyyy-MM-dd h:mm", new Date(datetimestamp));
        assertEquals("2012-06-23 11:00", date);

        int duration = (int) ((cursor.getLong(cursor.getColumnIndex(Talks.END_DATE)) - datetimestamp) / (60 * 60 * 1000));

        assertEquals(1, duration);
        assertEquals("sala1", cursor.getString(cursor.getColumnIndex(Talks.ROOM)));
    }
    
    public void testGetMillis() throws Exception {
        String expected = "2012-02-29 10:30";

        long millis = mTalksHandler.getMillis(expected);
        Date date = new Date(millis);

        CharSequence actual = DateFormat.format("yyyy-MM-dd h:mm", date);
        assertEquals(expected,actual);
    }
}