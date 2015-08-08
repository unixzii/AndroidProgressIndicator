package com.cyandev.pi;

import android.graphics.Canvas;

/**
 * 
 * @author Cyandev
 *
 */
public abstract class IndicatorRenderer {
	
	private boolean mIsMeasured = false;
	
	/**
	 * Initialize the renderer. 
	 * @param config
	 */
	public abstract void init(StyleConfig config);
	
	/**
	 * Calculate the relative dimension when the size of view is changed.
	 * @param width
	 * @param height
	 */
	public void measure(int width, int height) {
		mIsMeasured = true;
	}
	
	public boolean isMeasured() {
		return mIsMeasured;
	}
	
	/**
	 * Jump to next frame
	 * @return Return true if view need redrawing.
	 */
	public abstract boolean nextFrame();
	
	/**
	 * Render the current frame of the style.
	 * @param canvas
	 */
	public abstract void render(Canvas canvas);
}
