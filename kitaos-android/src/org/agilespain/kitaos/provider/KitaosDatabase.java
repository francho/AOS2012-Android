package org.agilespain.kitaos.provider;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Db helper
 */
class KitaosDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "kitaos.db";
    private static final int DATABASE_VERSION = 5;

    /**
     * config the database
     *
     * @param context the context
     */
    KitaosDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     *
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        if (db.isReadOnly()) {
            db = getWritableDatabase();
        }
        createTalks(db);
        createSpeakers(db);
    }

    /**
     * on db upgrade recreate all tables
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    /**
     * Create table Talks
     *
     * @param db the database
     */
    private void createTalks(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + Tables.TALKS + " ;");
        db.execSQL("CREATE TABLE " + Tables.TALKS + " (" +
                KitaosContract.Talks._ID + " INTEGER PRIMARY KEY," +
                KitaosContract.Talks.TITLE + " TEXT," +
                KitaosContract.Talks.DESCRIPTION + " TEXT," +
                KitaosContract.Talks.START_DATE + " LONG," +
                KitaosContract.Talks.END_DATE + " LONG," +
                KitaosContract.Talks.ROOM + " TEXT," +
                KitaosContract.Talks.SPEAKER + " TEXT," +
                KitaosContract.Talks.SPEAKER_EMAIL + " TEXT," +
                KitaosContract.Talks.SPEAKER_TWITTER + " TEXT" +
                ");");
    }

    private void createSpeakers(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + Tables.SPEAKERS + " ;");
        db.execSQL("CREATE TABLE " + Tables.SPEAKERS + " (" +
                KitaosContract.Speakers._ID + " INTEGER PRIMARY KEY," +
                KitaosContract.Speakers.FIRSTNAME + " TEXT," +
                KitaosContract.Speakers.LASTNAME + " TEXT," +
                KitaosContract.Speakers.EMAIL + " TEXT," +
                KitaosContract.Speakers.TWITTER + " TEXT," +
                KitaosContract.Speakers.BLOG + " TEXT" +
                ");");
    }

    interface Tables {
        String TALKS = "talks";
        String SPEAKERS = "speakers";
    }
}

