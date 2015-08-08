package com.cyandev.pi.styles;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.cyandev.pi.IndicatorRenderer;
import com.cyandev.pi.StyleConfig;
import com.cyandev.pi.utils.DisplayUnits;

public class DefaultStyle extends IndicatorRenderer {

	private StyleConfig mConfig;
	private Paint mRingPaint;
	private RectF mRect;
	private int mStart;
	private int mEnd;
	private int mFramePassed;
	private int mStartAcce;
	private int mEndAcce;
	private boolean mDirection;
	
	@Override
	public void init(StyleConfig config) {
		mConfig = config;
		mRingPaint = new Paint();
		
		mRingPaint.setColor(mConfig.majorColor);
		mRingPaint.setStyle(Paint.Style.STROKE);
		mRingPaint.setStrokeWidth(DisplayUnits.dp2px(mConfig.context, 3.0f));
		mRingPaint.setAntiAlias(true);
		
		mStart = 0;
		mEnd = 45;
		mFramePassed = 0;
		mStartAcce = 4;
		mEndAcce = 4;
	}

	@Override
	public void measure(int width, int height) {
		super.measure(width, height);
		
		float padding = DisplayUnits.dp2px(mConfig.context, 3.0f);
		mRect = new RectF(padding, padding, width - padding, height - padding);
	}

	@Override
	public boolean nextFrame() {
		mStart += mStartAcce;
		mEnd += mEndAcce;
		mFramePassed++;
		if (mFramePassed >= 40 && mFramePassed < 56) {
			mEndAcce++;
		} else if (mFramePassed >= 56 && mFramePassed < 72) {
			mEndAcce--;
		} else if (mFramePassed >= 112 && mFramePassed < 128) {
			mStartAcce++;
		} else if (mFramePassed >= 128 && mFramePassed < 144) {
			mStartAcce--;
		} else if (mFramePassed > 144) {
			mFramePassed = 0;
		}
		
		if (mEnd > 360) {
			mEnd = mEnd % 360;
			mDirection = true;
		}
		
		if (mStart > 360) {
			mStart = mStart % 360;
			mDirection = false;
		}

		return true;
	}
	
	@Override
	public void render(Canvas canvas) {
		int sweep = 0;
		if (mDirection) {
			sweep = 360 - mStart + mEnd;
		} else {
			sweep = mEnd - mStart;
		}
		canvas.drawArc(mRect, mStart - 90, sweep, false, mRingPaint);
	}

}
