/**
 *  ZgzBus - Consulta cuando llega el autobus urbano en Zaragoza
 *  Copyright (C) 2010 Francho Joven
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.agilespain.kitaos.provider;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

/**
 * Content provider for kitaos
 *
 * @author francho - http://francho.org/lab/
 */
public class KitaosProvider extends ContentProvider {


    private static final int TALKS = 1;
    private static final int TALKS_ID = 2;
    private static final int TALKS_HOURS = 3;
    private static final int SPEAKERS = 4;
    private static final int SPEAKERS_ID = 5;
    private static final int ROOMS = 6;

    /**
     * Configuramos las urls disponibles
     */
    private static final UriMatcher sUriMatcher;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        sUriMatcher.addURI(KitaosContract.CONTENT_AUTHORITY, "talks_hours", TALKS_HOURS);
        sUriMatcher.addURI(KitaosContract.CONTENT_AUTHORITY, "talks/#", TALKS_ID);
        sUriMatcher.addURI(KitaosContract.CONTENT_AUTHORITY, "talks", TALKS);
        sUriMatcher.addURI(KitaosContract.CONTENT_AUTHORITY, "speakers/#", SPEAKERS_ID);
        sUriMatcher.addURI(KitaosContract.CONTENT_AUTHORITY, "speakers", SPEAKERS);
        sUriMatcher.addURI(KitaosContract.CONTENT_AUTHORITY, "rooms", ROOMS);
    }


    private KitaosDatabase mOpenHelper;

    /**
     * On create...
     */
    @Override
    public boolean onCreate() {
        mOpenHelper = new KitaosDatabase(getContext());
        return true;
    }

    /**
     * query...
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch (sUriMatcher.match(uri)) {

            case SPEAKERS:
                qb.setTables(KitaosDatabase.Tables.SPEAKERS);
                break;
            case SPEAKERS_ID:
                qb.setTables(KitaosDatabase.Tables.SPEAKERS);
                qb.appendWhere(KitaosContract.Speakers._ID + "=" + uri.getPathSegments().get(1));
                break;

            case TALKS:
                qb.setTables(KitaosDatabase.Tables.TALKS);
                break;
            case TALKS_ID:
                qb.setTables(KitaosDatabase.Tables.TALKS);
                qb.appendWhere(KitaosContract.Talks._ID + "=" + uri.getPathSegments().get(1));
                break;

            case TALKS_HOURS:
                qb.setTables(KitaosDatabase.Tables.TALKS);
                projection = new String[]{KitaosContract.Talks.START_DATE, KitaosContract.Talks.START_DATE + " as " + BaseColumns._ID};
                qb.setDistinct(true);
                break;
            case ROOMS:
                qb.setTables(KitaosDatabase.Tables.TALKS);
                projection = new String[]{KitaosContract.Talks.ROOM};
                qb.setDistinct(true);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // Get the database and run the query
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        Log.d("**debug**", c.toString());
        // Tell the cursor what uri to watch, so it knows when its source data changes
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    /**
     * getType...
     */
    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case TALKS:
                return KitaosContract.Talks.CONTENT_TYPE;
            case TALKS_ID:
                return KitaosContract.Talks.ITEM_CONTENT_TYPE;
            case SPEAKERS:
                return KitaosContract.Speakers.CONTENT_TYPE;
            case SPEAKERS_ID:
                return KitaosContract.Speakers.ITEM_CONTENT_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    /**
     * insert...
     */
    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        // Validate the requested uri
        if (sUriMatcher.match(uri) != TALKS && sUriMatcher.match(uri) != SPEAKERS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        //Long now = Long.valueOf(System.currentTimeMillis());

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        if (sUriMatcher.match(uri) == TALKS) {
            long rowId = db.insert(KitaosDatabase.Tables.TALKS, null, values);
            if (rowId > 0) {
                Uri myUri = ContentUris.withAppendedId(KitaosContract.Talks.CONTENT_URI, rowId);
                getContext().getContentResolver().notifyChange(myUri, null);
                getContext().getContentResolver().notifyChange(KitaosContract.Talks.uri(), null);
                getContext().getContentResolver().notifyChange(KitaosContract.Talks.hoursUri(), null);
                return myUri;
            }
        } else if (sUriMatcher.match(uri) == SPEAKERS) {
            long rowId = db.insert(KitaosDatabase.Tables.SPEAKERS, null, values);
            if (rowId > 0) {
                Uri myUri = ContentUris.withAppendedId(KitaosContract.Speakers.CONTENT_URI, rowId);
                getContext().getContentResolver().notifyChange(myUri, null);
                return myUri;
            }
        }
        throw new SQLException("Failed to insert row into " + uri);
    }

    /**
     * delete
     */
    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        int count;
        switch (sUriMatcher.match(uri)) {
            case SPEAKERS_ID:
                count = db.delete(KitaosDatabase.Tables.SPEAKERS,
                        KitaosContract.Speakers._ID + "=" + uri.getPathSegments().get(1)
                                + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
                break;
            case SPEAKERS:
                count = db.delete(KitaosDatabase.Tables.SPEAKERS, where, whereArgs);
                break;
            case TALKS:
                count = db.delete(KitaosDatabase.Tables.TALKS, where, whereArgs);
                break;
            case TALKS_ID:
                count = db.delete(KitaosDatabase.Tables.TALKS,
                        KitaosContract.Talks._ID + "=" + uri.getPathSegments().get(1)
                                + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    /**
     * update
     */
    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case SPEAKERS_ID:
                count = db.update(KitaosDatabase.Tables.SPEAKERS, values,
                        KitaosContract.Speakers._ID + "=" + uri.getPathSegments().get(1)
                                + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
                break;
            case SPEAKERS:
                count = db.update(KitaosDatabase.Tables.SPEAKERS, values, where, whereArgs);
                break;
            case TALKS:
                count = db.update(KitaosDatabase.Tables.TALKS, values, where, whereArgs);
                break;
            case TALKS_ID:
                count = db.update(KitaosDatabase.Tables.TALKS, values,
                        KitaosContract.Talks._ID + "=" + uri.getPathSegments().get(1)
                                + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {

        switch (sUriMatcher.match(uri)) {
            case TALKS:
                break;
            case SPEAKERS:
                break;
        }

        return super.bulkInsert(uri, values);
    }


}
