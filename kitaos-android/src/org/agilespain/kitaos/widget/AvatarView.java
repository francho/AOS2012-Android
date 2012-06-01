/**
 *  Copyright (C) 2011 Francho Joven (http://francho.org)
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
package org.agilespain.kitaos.widget;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.widget.ImageView;
import org.agilespain.kitaos.R;

/**
 * 
 * 
 * @author http://francho.org
 * 
 */
public class AvatarView extends ImageView {
	/** for logs */
	private static final String LOGTAG = "AvatarView";

	/** AsyncTask for load in background */
	private DownloadAvatarAsyncTask currentTask;

	/**
	 * Constructor
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public AvatarView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setImageResource(R.drawable.unknown_photo);
	}

	/**
	 * Constructor
	 * @param context
	 * @param attrs
	 */
	public AvatarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setImageResource(R.drawable.unknown_photo);
	}

	/**
	 * Contructor
	 * @param context
	 */
	public AvatarView(Context context) {
		super(context);
	}

	/**
	 * Download and cache a remote image
	 * @param email
	 */
	public void setAvatar(String email) {
        try {
            String cacheFile = getContext().getApplicationContext().getCacheDir()+email;
            Bitmap bm = BitmapFactory.decodeFile(cacheFile) ;
            setImageBitmap(bm);
        } catch(Exception e) {
            setImageResource(R.drawable.unknown_photo);
        }
    }

}