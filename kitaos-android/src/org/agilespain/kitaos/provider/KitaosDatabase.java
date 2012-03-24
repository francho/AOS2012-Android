package org.agilespain.kitaos.provider;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Db helper
 */
class KitaosDatabase extends SQLiteOpenHelper {
	public static final String DATABASE_NAME = "kitaos.db";
    public static final int DATABASE_VERSION = 3;
	
	/**
	 * config the database
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
    	if(db.isReadOnly()) { db=getWritableDatabase(); }
        createTalks(db);
    }

    /**
     *
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	//if(db.isReadOnly()) { db=getWritableDatabase(); }
        
//    	switch(newVersion) {
//    	case 2:
//    		//createTalks(db);
//    	}

        createTalks(db);
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
                KitaosContract.Talks.START_DATE + " LONG," +
                KitaosContract.Talks.END_DATE + " LONG," +
                KitaosContract.Talks.ROOM + " TEXT," +
                KitaosContract.Talks.SPEAKER + " TEXT" +
        		");");
    }
    

    
    interface Tables {
        String TALKS = "talks";
    }
}

