package com.cyandev.pi.utils;

import android.content.Context;

/**
 * 
 * @author Cyandev
 *
 */
public final class DisplayUnits {
	
	/**
	 * Convert dp to px.
	 * @param context
	 * @param dp
	 * @return
	 */
	public static float dp2px(Context context, float dp) {
		final float scale = context.getResources().getDisplayMetrics().scaledDensity;
		return dp * scale;
	}
}
