package org.agilespain.kitaos.widget;

import android.content.Context;
import android.graphics.Typeface;

/**
 * @author francho (http://francho.org)
 */
public class TypefaceUtils {
    public static Typeface getNormalFont(Context context) {
        String ttf = "fonts/TulpenOne-Regular.ttf";
        return Typeface.createFromAsset(context.getAssets(), ttf);
    }

    public static Typeface getTitleFont(Context context) {
        String ttf = "fonts/GochiHand-Regular.ttf";
        return Typeface.createFromAsset(context.getAssets(), ttf);
    }
}
