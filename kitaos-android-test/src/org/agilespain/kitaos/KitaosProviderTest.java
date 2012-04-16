package org.agilespain.kitaos.test;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;
import org.agilespain.kitaos.provider.KitaosContract;
import org.agilespain.kitaos.provider.KitaosProvider;


/**
 * Test the content provider
 * @author francho
 */
public class KitaosProviderTest extends ProviderTestCase2<KitaosProvider> {


    private KitaosProvider mProvider;

    public KitaosProviderTest(){
        super(KitaosProvider.class, KitaosContract.CONTENT_AUTHORITY);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // mContext = getMockContext();
        // mResolver = getMockContentResolver();
        // mDb = getProvider().getDatabase();
        mProvider = getProvider();
    }

    public void testQuery() {
        Uri uri = KitaosContract.Talks.uri(2);

        Cursor cursor = mProvider.query(uri, null, null, null, null);
        assertNotNull(cursor);

        try {
            mProvider.query(Uri.parse("definitelywrong"), null, null, null, null);
            // we're wrong if we get until here!
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
        
        
        Uri uri1 = KitaosContract.Speakers.uri(2);

        Cursor cursor1 = mProvider.query(uri1, null, null, null, null);
        assertNotNull(cursor1);

        try {
            mProvider.query(Uri.parse("definitelywrong"), null, null, null, null);
            // we're wrong if we get until here!
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }
    
    public void testInsertSpeaker() throws Exception {
        final ContentValues values = new ContentValues();

        int id = 123;
        String firstName = "David";
        String lastName="Pina López";
        String email = "dpinalopez68@gmail.com";
        String twitter ="@dpinalopez68";
        String blog ="http://dpinalopez68.wordpress.com/";
        
        

        values.put(KitaosContract.Speakers._ID, id);
        values.put(KitaosContract.Speakers.FIRSTNAME, firstName);
        values.put(KitaosContract.Speakers.LASTNAME, lastName);
        values.put(KitaosContract.Speakers.EMAIL, email);
        values.put(KitaosContract.Speakers.TWITTER, twitter);
        values.put(KitaosContract.Speakers.BLOG, blog);

        mProvider.insert(KitaosContract.Speakers.uri(),values);

        String[] projection = {
                KitaosContract.Speakers._ID,
                KitaosContract.Speakers.FIRSTNAME,
                KitaosContract.Speakers.LASTNAME,
                KitaosContract.Speakers.EMAIL,
                KitaosContract.Speakers.TWITTER,
                KitaosContract.Speakers.BLOG,
        };
        Cursor cursor = mProvider.query(KitaosContract.Speakers.uri(id),projection,null,null,null);
        cursor.moveToFirst();
        
        assertEquals(id, cursor.getInt(0));
        assertEquals(firstName, cursor.getString(1));
        assertEquals(lastName, cursor.getString(2));
        assertEquals(email, cursor.getString(3));
        assertEquals(twitter, cursor.getString(4));
        assertEquals(blog, cursor.getString(5));

        cursor.close();
        
        
    }
    
    
    public void testInsert() throws Exception {
        final ContentValues values = new ContentValues();

        int id = 123;
        String title = "title";
        String date = "2012-05-23 10:30";
        double duration = 2;
        String room = "room";
        String speaker = "speaker";

        values.put(KitaosContract.Talks._ID, id);
        values.put(KitaosContract.Talks.TITLE, title);
        values.put(KitaosContract.Talks.START_DATE, date);
        values.put(KitaosContract.Talks.END_DATE, duration);
        values.put(KitaosContract.Talks.ROOM, room);
        values.put(KitaosContract.Talks.SPEAKER, speaker);

        mProvider.insert(KitaosContract.Talks.uri(),values);

        String[] projection = {
                KitaosContract.Talks._ID,
                KitaosContract.Talks.TITLE,
                KitaosContract.Talks.START_DATE,
                KitaosContract.Talks.END_DATE,
                KitaosContract.Talks.ROOM,
                KitaosContract.Talks.SPEAKER,
        };
        Cursor cursor = mProvider.query(KitaosContract.Talks.uri(id),projection,null,null,null);
        cursor.moveToFirst();
        
        assertEquals(id, cursor.getInt(0));
        assertEquals(title, cursor.getString(1));
        assertEquals(date, cursor.getString(2));
        assertEquals(duration, cursor.getDouble(3));
        assertEquals(room, cursor.getString(4));
        assertEquals(speaker, cursor.getString(5));

        cursor.close();
        
        
    }

    
    public void testDeleteSpeaker() throws Exception {
        int id = 999;
        final ContentValues values = new ContentValues();
        values.put(KitaosContract.Speakers._ID, id);

        Cursor cursor;

        mProvider.insert(KitaosContract.Speakers.uri(),values);
        cursor = mProvider.query(KitaosContract.Speakers.uri(id),null,null,null,null);
        assertEquals(1,cursor.getCount());

        mProvider.delete(KitaosContract.Speakers.uri(id),null,null);
        cursor = mProvider.query(KitaosContract.Speakers.uri(id),null,null,null,null);
        assertEquals(0,cursor.getCount());
    }
    
    public void testDelete() throws Exception {
        int id = 999;
        final ContentValues values = new ContentValues();
        values.put(KitaosContract.Talks._ID, id);

        Cursor cursor;

        mProvider.insert(KitaosContract.Talks.uri(),values);
        cursor = mProvider.query(KitaosContract.Talks.uri(id),null,null,null,null);
        assertEquals(1,cursor.getCount());

        mProvider.delete(KitaosContract.Talks.uri(id),null,null);
        cursor = mProvider.query(KitaosContract.Talks.uri(id),null,null,null,null);
        assertEquals(0,cursor.getCount());
    }

    public void testUpdate() throws Exception {
        final int id = 343;
        final Uri uri = KitaosContract.Talks.uri(id);
        final ContentValues values = new ContentValues();

        values.put(KitaosContract.Talks._ID, id);
        values.put(KitaosContract.Talks.TITLE, "title");

        mProvider.insert(KitaosContract.Talks.uri(),values);

        values.put(KitaosContract.Talks.TITLE, "Hi dude");
        mProvider.update(uri, values, null, null);

        Cursor cursor = mProvider.query(uri,null,null,null,null);
        cursor.moveToFirst();
        assertEquals("Hi dude", cursor.getString(cursor.getColumnIndex(KitaosContract.Talks.TITLE)));
    }

    public void testUpdateSpeakers() throws Exception {
        final int id = 343;
        final Uri uri = KitaosContract.Speakers.uri(id);
        final ContentValues values = new ContentValues();

        values.put(KitaosContract.Speakers._ID, id);
        values.put(KitaosContract.Speakers.FIRSTNAME, "David");

        mProvider.insert(KitaosContract.Speakers.uri(),values);

        values.put(KitaosContract.Speakers.FIRSTNAME, "Francho");
        mProvider.update(uri, values, null, null);

        Cursor cursor = mProvider.query(uri,null,null,null,null);
        cursor.moveToFirst();
        assertEquals("Francho", cursor.getString(cursor.getColumnIndex(KitaosContract.Speakers.FIRSTNAME)));
    }
    
    
    
    public void testBulkInsert() throws Exception {

    }
}
