
package org.agilespain.kitaos.service;

import android.content.ContentProviderOperation;
import android.content.ContentProviderOperation.Builder;
import android.content.ContentResolver;
import org.agilespain.kitaos.provider.KitaosContract;
import org.agilespain.kitaos.provider.KitaosContract.Talks;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

/**
 * Parse the json and generate the sql sentences to save it into the db
 */
public class TalksJsonHandler extends JsonHandler {

    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

	public TalksJsonHandler() {
		super(KitaosContract.CONTENT_AUTHORITY);
	}

	// private static final String TAG = "TalksJsonHandler";

    
	/* (non-Javadoc)
	 * Parser a json like:
	 *
	 * { "Android" : { "date" : "2012-06-23",
     *       "duration" : 1,
     *       "hour" : 11,
     *       "room" : { "name" : "sala1" },
     *   "time" : "11:0",
     *           "title" : "Android"
     * }, {
     *   "Kanban" : { "date" : "2012-06-23",
     *       "duration" : 1,
     *       "hour" : 9,
     *       "room" : { "name" : "sala2" },
     *   "time" : "9:0",
     *           "title" : "Kanban"
     * }
     */
	@Override
	public ArrayList<ContentProviderOperation> parse(String jsonString,
			ContentResolver resolver) throws JSONException, IOException {
        final ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();

        Builder builder = ContentProviderOperation.newDelete(Talks.uri());
        batch.add(builder.build());

        final JSONObject jsonResponse = new JSONObject(jsonString);

        Iterator it = jsonResponse.keys();
        while(it.hasNext()) {
            String key = (String) it.next();

            int id = Integer.parseInt(key);

            JSONObject curTalk = (JSONObject) jsonResponse.get(key);

            builder = ContentProviderOperation.newInsert(Talks.uri());
            builder.withValue(Talks._ID, id);
            builder.withValue(Talks.TITLE, curTalk.getString(TalksJson.TITLE).trim());
            builder.withValue(Talks.ROOM, curTalk.getJSONObject(TalksJson.ROOM).getString(TalksJson.NAME).trim());
            
            String date = curTalk.getString(TalksJson.DATE).trim() + " " + normalizeTime(curTalk.getString(TalksJson.HOUR)) ;

            // Dates to millis
            long millisStart = getMillis(date);
            builder.withValue(Talks.START_DATE, millisStart);

            builder.withValue(Talks.END_DATE, getEndMillis(millisStart, curTalk.getInt(TalksJson.DURATION)));

            // TODO
            // builder.withValue(Talks.SPEAKER, curTalk.getString(TalksJson.SPEAKER).trim());

            batch.add(builder.build());
        }
        return batch;
    }
    
    public long getMillis(String dateString) {
        try {
            Date date = df.parse(dateString);
            return date.getTime() ;
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.

            return -1;
        }
    }

    public long getEndMillis(long millisStart, int duration) {
        return (duration * 60 * 60 * 1000) + millisStart;
    }
    
    private String normalizeTime(String time) {
        int hour, min;
        if(time.contains(":")) {
            String[] parts = time.trim().split(":");
            hour = Integer.parseInt(parts[0]);
            min = Integer.parseInt(parts[1]);
        } else {
            hour = Integer.parseInt(time);
            min = 0;
        }
        return String.format("%02d:%02d", hour, min);
    }


    interface TalksJson {
    	String DATE = "date";
    	String DURATION = "duration";
        String HOUR = "time";
        String ROOM = "room";
        String NAME = "name";
        // String TIME = "time";
        String TITLE = "title";
    }

}
