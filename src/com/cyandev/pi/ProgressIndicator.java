package com.cyandev.pi;

import com.cyandev.pi.styles.DefaultStyle;
import com.cyandev.pi.utils.DisplayUnits;
import com.cyandev.progressindicators.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;

/**
 * 
 * @author Cyandev
 *
 */
public class ProgressIndicator extends View {

	/**
	 * The major color of the indicator.
	 */
	private int mMajorColor = Color.GRAY;
	
	/**
	 * The minor color of the indicator.
	 */
	private int mMinorColor = Color.LTGRAY;
	
	/**
	 * The renderer which renders the specific style.
	 */
	private IndicatorRenderer mRenderer;
	
	private Handler mHandler = new Handler() {
		
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case InternalThread.MESSAGE_NEXTFRAME:
				if (mRenderer != null) {
					if (mRenderer.isMeasured()) {
						if (mRenderer.nextFrame()) {
							invalidate();
						}
					}
				}
				if (mThread != null) {
					if (mThread.isAlive()) {
						mThread.getHandler().sendEmptyMessageDelayed(InternalThread.MESSAGE_NEXTFRAME, 16);
					}
				}
				break;
			}
		};
	};
	
	private InternalThread mThread;
	
	public ProgressIndicator(Context context, AttributeSet attrs,
			int defStyleAttr) throws Exception {
		super(context, attrs, defStyleAttr);
				
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ProgressIndicator);
		mMajorColor = a.getColor(R.attr.major_color, Color.GRAY);
		mMinorColor = a.getColor(R.attr.minor_color, Color.LTGRAY);
		mRenderer = getRendererByStyleName(a.getString(R.attr.style_name));
		
		a.recycle();
		
		init();
	}

	public ProgressIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		init();
	}

	public ProgressIndicator(Context context) {
		super(context);
		
		init();
	}

	private void init() {
		if (mRenderer == null) {
			try {
				mRenderer = new DefaultStyle();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		initRenderer();
	}
	
	private void initRenderer() {
		StyleConfig config = new StyleConfig();
		
		config.majorColor = mMajorColor;
		config.minorColor = mMinorColor;
		config.extraFlags = 0;
		config.context = getContext();
		
		mRenderer.init(config);
	}

	public void setStyle(IndicatorRenderer renderer) {
		mRenderer = renderer;
		initRenderer();
		mRenderer.measure(getWidth(), getHeight());
		invalidate();
	}
	
	
	
	@Override
	protected void onAttachedToWindow() {
		if (mThread == null) {
			mThread = new InternalThread();
		}
		
		if (!mThread.isAlive()) {
			mThread.start();
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		if (mThread != null) {
			if (mThread.isAlive()) {
				mThread.getHandler().sendEmptyMessage(InternalThread.MESSAGE_QUIT);
				try {
					mThread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				mThread = null;
			}
		}
		
		super.onDetachedFromWindow();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mRenderer != null) {
			if (mRenderer.isMeasured()) {
				mRenderer.render(canvas);
			}
		}
	}
	
	@Override
	protected int getSuggestedMinimumHeight() {
		return (int) DisplayUnits.dp2px(getContext(), 50);
	}
	
	@Override
	protected int getSuggestedMinimumWidth() {
		return (int) DisplayUnits.dp2px(getContext(), 50);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = measureInternal(widthMeasureSpec, true);
		int height = measureInternal(heightMeasureSpec, false);
		setMeasuredDimension(width, height);
		mRenderer.measure(width, height);
	}
	
	private int measureInternal(int measureSpec, boolean isWidth) {
		int mode = MeasureSpec.getMode(measureSpec);
		int size = MeasureSpec.getSize(measureSpec);
		int padding = isWidth ? getPaddingLeft() + getPaddingRight() : getPaddingTop() + getPaddingBottom();
		
		switch (mode) {
		case MeasureSpec.EXACTLY:
			return size;
		default:
			int result = getSuggestedMinimumWidth() + padding;
			if (mode == MeasureSpec.AT_MOST) {
				result = Math.max(result, size);
			}
			return result;
		}
	}
		
	private IndicatorRenderer getRendererByStyleName(String styleName) throws Exception {
		if (styleName == "") {
			return null;
		} else {
			try {
				String realClassName;
				if (styleName.startsWith("com.")) {
					realClassName = styleName;
				} else {
					realClassName = "com.cyandev.pi.styles" + styleName;
				}
				Class<?> klass = Class.forName(realClassName);
				IndicatorRenderer renderer = (IndicatorRenderer) klass.newInstance();
				return renderer;
			} catch (ClassNotFoundException e) {
				throw e;
			}
		}
	}
	
	private class InternalThread extends Thread {
		
		public final static int MESSAGE_QUIT = 0;
		public final static int MESSAGE_NEXTFRAME = 1;
		
		private Handler mThreadHandler;
		
		@Override
		public void run() {
			Looper.prepare();
			
			mThreadHandler = new Handler() {
				
				@Override
				public void handleMessage(android.os.Message msg) {
					switch (msg.what) {
					case MESSAGE_QUIT:
						Looper.myLooper().quit();
						break;
					case MESSAGE_NEXTFRAME:
						if (mHandler != null) {
							mHandler.sendEmptyMessage(MESSAGE_NEXTFRAME);
						}
						break;
					}
				};
			};
			
			mThreadHandler.sendEmptyMessage(InternalThread.MESSAGE_NEXTFRAME);
			
			Looper.loop();
		}
		
		public Handler getHandler() {
			return mThreadHandler;
		}
	}
}
