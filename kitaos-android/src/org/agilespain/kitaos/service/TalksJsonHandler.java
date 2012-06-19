
package org.agilespain.kitaos.service;

import android.content.ContentProviderOperation;
import android.content.ContentProviderOperation.Builder;
import android.content.ContentResolver;
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

    private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

	public TalksJsonHandler() {
		super();
	}

	// private static final String TAG = "TalksJsonHandler";

    
	/* (non-Javadoc)
	 * Parser a json like:
	 *
	  "23001" : { "date" : "2012-06-23",
      "description" : "¿Es libre o no?",
      "duration" : 1,
      "room" : { "name" : "sala2" },
      "session" : 1,
      "speaker" : { "city" : "Pamplona",
          "computers_needed" : true,
          "email" : "richard@gnu.org",
          "first_name" : "Richard",
          "last_name" : "Stallman",
          "speaker" : true,
          "twitter_id" : "GNUplusLINUX"
        },
      "time" : "9:30",
      "title" : "TDD"
    },
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

            if(curTalk.has(Talks.DESCRIPTION)) {
                builder.withValue(Talks.DESCRIPTION, curTalk.getString(TalksJson.DESCRIPTION).trim());
            }

            builder.withValue(Talks.ROOM, curTalk.getJSONObject(TalksJson.ROOM).getString(TalksJson.NAME).trim());

            String date = curTalk.getString(TalksJson.DATE).trim() + " " + normalizeTime(curTalk.getString(TalksJson.HOUR)) ;

            // Dates to millis
            long millisStart = getMillis(date);
            builder.withValue(Talks.START_DATE, millisStart);

            builder.withValue(Talks.END_DATE, getEndMillis(millisStart, curTalk.getInt(TalksJson.DURATION)));

            // TODO

            JSONObject jsonSpeaker = curTalk.getJSONObject(TalksJson.SPEAKER);

            String name =  jsonSpeaker.getString(TalksJson.SPEAKER_FIRST_NAME).trim() + " "
                    + jsonSpeaker.getString(TalksJson.SPEAKER_LAST_NAME).trim();

            builder.withValue(Talks.SPEAKER, name);
            builder.withValue(Talks.SPEAKER_EMAIL, jsonSpeaker.getString(TalksJson.SPEAKER_EMAIL).trim());
            builder.withValue(Talks.SPEAKER_TWITTER, jsonSpeaker.getString(TalksJson.SPEAKER_TWITTER).trim());

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

    long getEndMillis(long millisStart, int duration) {
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
        String DESCRIPTION = "description";
        String SPEAKER = "speaker";
        String SPEAKER_FIRST_NAME = "first_name";
        String SPEAKER_LAST_NAME = "last_name";
        String SPEAKER_EMAIL = "email";
        String SPEAKER_TWITTER = "twitter_id";
    }

}
