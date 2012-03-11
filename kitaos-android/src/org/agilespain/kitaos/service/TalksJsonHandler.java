
package org.agilespain.kitaos.service;

import android.content.ContentProviderOperation;
import android.content.ContentProviderOperation.Builder;
import android.content.ContentResolver;
import org.agilespain.kitaos.provider.KitaosContract;
import org.agilespain.kitaos.provider.KitaosContract.Talks;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Parse the json and generate the sql sentences to save it into the db
 */
public class TalksJsonHandler extends JsonHandler {

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
            
            builder.withValue(Talks.DATE, date);
            builder.withValue(Talks.DURATION, curTalk.getString(TalksJson.DURATION).trim());

            // TODO
            // builder.withValue(Talks.SPEAKER, curTalk.getString(TalksJson.SPEAKER).trim());

            batch.add(builder.build());
        }
        return batch;
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
        String HOUR = "hour";
        String ROOM = "room";
        String NAME = "name";
        // String TIME = "time";
        String TITLE = "title";
    }

}
