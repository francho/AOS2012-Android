package org.agilespain.kitaos.widget;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by IntelliJ IDEA.
 * User: francho
 * Date: 24/03/12
 * Time: 00:31
 * To change this template use File | Settings | File Templates.
 */
public class TypefaceUtils {
    public static Typeface getNormalFont(Context context) {
        String ttf = "fonts/ShadowsIntoLightTwo-Regular.ttf";
        return Typeface.createFromAsset(context.getAssets(), ttf);
    }

    public static Typeface getTitleFont(Context context) {
        String ttf = "fonts/GochiHand-Regular.ttf";
        return Typeface.createFromAsset(context.getAssets(), ttf);
    }
}
