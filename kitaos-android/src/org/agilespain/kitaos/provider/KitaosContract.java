/**
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

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Adapter for KitAOS
 *
 * @author francho - http://francho.org/lab/
 */
public class KitaosContract {
    public static final String CONTENT_AUTHORITY = "org.agilespain.kitaos";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    private static final String PATH_TALKS = "talks";
    private static final String PATH_TALKS_HOURS = "talks_hours";
    private static final String PATH_SPEAKERS = "speakers";
    private static final String PATH_ROOMS = "rooms";

    // This class cannot be instantiated
    private KitaosContract() {
    }

    public static final class Talks implements BaseColumns {
        protected static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TALKS).build();
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/org.agilespain.kitaos.talks";
        public static final String ITEM_CONTENT_TYPE = "vnd.android.cursor.item/org.agilespain.kitaos.talks";

        private Talks() {
        }

        public static final String TITLE = "title";
        public static final String START_DATE = "start_date";
        public static final String END_DATE = "end_date";
        public static final String ROOM = "room";
        public static final String SPEAKER = "speaker";


        /**
         * Build {@link android.net.Uri} that references any {@link Talks} associated
         * with the requested {@link #_ID}.
         *
         * @param id talk id
         * @return the uri with the id
         */
        public static Uri uri(int id) {
            return CONTENT_URI.buildUpon().appendPath(""+id).build();
        }

        public static Uri uri() {
            return CONTENT_URI;
        }
        
        public static Uri hoursUri() {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_TALKS_HOURS).build();
        }

        public static Uri roomsUri() {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_ROOMS).build();
        }
    }
    
    
    public static final class Speakers implements BaseColumns {
        protected static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SPEAKERS).build();
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/org.agilespain.kitaos.speakers";
        public static final String ITEM_CONTENT_TYPE = "vnd.android.cursor.item/org.agilespain.kitaos.speakers";

        private Speakers() {
        }

        public static final String FIRSTNAME = "firstname";
        public static final String LASTNAME = "lastname";
        public static final String EMAIL = "email";
        public static final String TWITTER = "twitter";
        public static final String BLOG = "blog";
      


        
        public static Uri uri(int id) {
            return CONTENT_URI.buildUpon().appendPath(""+id).build();
        }

        public static Uri uri() {
            return CONTENT_URI;
        }
        
    }
    
}


