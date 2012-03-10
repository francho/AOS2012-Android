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

    private static final UriMatcher sUriMatcher;

    /**
     * Configuramos las urls disponibles
     */
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        sUriMatcher.addURI(KitaosContract.CONTENT_AUTHORITY, "talks/#", TALKS_ID);
        sUriMatcher.addURI(KitaosContract.CONTENT_AUTHORITY, "talks", TALKS);
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
        qb.setTables(KitaosDatabase.Tables.TALKS);

        switch (sUriMatcher.match(uri)) {
            case TALKS:
                break;

            case TALKS_ID:
                qb.appendWhere(KitaosContract.Talks._ID + "=" + uri.getPathSegments().get(1));
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
        if (sUriMatcher.match(uri) != TALKS) {
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

        long rowId = db.insert(KitaosDatabase.Tables.TALKS, null, values);
        if (rowId > 0) {
            Uri myUri = ContentUris.withAppendedId(KitaosContract.Talks.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(myUri, null);
            return myUri;
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
            case TALKS:
                count = db.delete(KitaosDatabase.Tables.TALKS, where, whereArgs);
                break;

            case TALKS_ID:
                String favoritosId = uri.getPathSegments().get(1);
                count = db.delete(KitaosDatabase.Tables.TALKS, KitaosContract.Talks._ID + "=" + favoritosId
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
            case TALKS:
                count = db.update(KitaosDatabase.Tables.TALKS, values, where, whereArgs);
                break;

            case TALKS_ID:
                String myId = uri.getPathSegments().get(1);
                count = db.update(KitaosDatabase.Tables.TALKS, values, KitaosContract.Talks._ID + "=" + myId
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
        }

        return super.bulkInsert(uri, values);
    }


}
