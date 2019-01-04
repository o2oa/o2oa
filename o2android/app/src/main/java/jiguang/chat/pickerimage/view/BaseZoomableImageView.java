package jiguang.chat.pickerimage.view;

/*
 * Copyright 2012 Laurence Dawson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * This class is based upon the file ImageViewTouchBase.java which can be found at:
 * https://dl-ssl.google.com/dl/googlesource/git-repo/repo
 *  
 * Copyright (C) 2009 The Android Open Source Project
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import jiguang.chat.pickerimage.utils.SampleSizeUtil;


public abstract class BaseZoomableImageView extends View {
	// Statics
	static final float sPanRate = 7;
	static final float sScaleRate = 1.25F;
	static final int sPaintDelay = 250;
	static final int sAnimationDelay = 500;
	public static final int MIN_SDK_ENABLE_LAYER_TYPE_HARDWARE = Build.VERSION_CODES.ICE_CREAM_SANDWICH;

	// 横屏时特殊处理,但高大于宽的2倍时，就以宽充满
	private static final float MAX_IMAGE_RATIO_WIDTH_LARGE_LANDSCAPE = 2F;

	private static final float MAX_IMAGE_RATIO_LARGE = 5F;

	// This is the base transformation which is used to show the image
	// initially.  The current computation for this shows the image in
	// it's entirety, letterboxing as needed.  One could chose to
	// show the image as cropped instead.  
	//
	// This matrix is recomputed when we go from the thumbnail image to
	// the full size image.
	private Matrix mBaseMatrix = new Matrix();

	// This is the supplementary transformation which reflects what 
	// the user has done in terms of zooming and panning.
	//
	// This matrix remains the same when we go from the thumbnail image
	// to the full size image.
	private Matrix mSuppMatrix = new Matrix();

	// This is the final matrix which is computed as the concatentation
	// of the base matrix and the supplementary matrix.
	private Matrix mDisplayMatrix = new Matrix();

	// A replacement ImageView matrix
	private Matrix mMatrix = new Matrix();

	// Used to filter the bitmaps when hardware acceleration is not enabled
	private Paint mPaint;

	// Temporary buffer used for getting the values out of a matrix.
	private float[] mMatrixValues = new float[9];

	// Dimensions for the view
	private int mThisWidth = -1, mThisHeight = -1;

	// The max zoom for the view, determined programatically
	private float mMaxZoom;

	// If not null, calls setImageBitmap when onLayout is triggered
	private Runnable mOnLayoutRunnable = null;

	// Stacked to the internal queue to invalidate the view
	private Runnable mRefresh = null;

	// The time of the last draw operation
	private double mLastDraw = 0;

	// The current bitmap being displayed.
	protected Bitmap mBitmap;

	// Stacked to the internal queue to scroll the view
	private Runnable mFling = null;
	private boolean fling = false;

	// Single tap listener
	protected ImageGestureListener mImageGestureListener;

	protected ViewPager mViewPager;

	private boolean landscape = false;

	// Programatic entry point
	public BaseZoomableImageView(Context context) {
		super(context);
		initBaseZoomableImageView( context );
	}

	// XML entry point
	public BaseZoomableImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initBaseZoomableImageView( context );
	}

	// Setup the view
	@SuppressLint("NewApi")
	protected void initBaseZoomableImageView( Context context) {
		mPaint = new Paint();
		mPaint.setDither(true);
		mPaint.setFilterBitmap(true);
		mPaint.setAntiAlias(true);
		if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
			landscape = true;
		}else {
			landscape = false;
		}
		// Setup the refresh runnable
		mRefresh = new Runnable() {
			@Override
			public void run() {
				postInvalidate();
			}
		};		
	}

	// Set the single tap listener
	public void setImageGestureListener( ImageGestureListener listener ){
		this.mImageGestureListener = listener;
	}

	public void setViewPager(ViewPager viewPager) {
		this.mViewPager = viewPager;
	}

	// Get the bitmap for the view
	public Bitmap getImageBitmap(){
		return mBitmap;
	}

	// Free the bitmaps and matrices
	public void clear(){
		if(mBitmap!=null && !mBitmap.isRecycled()) {
			mBitmap.recycle();
		}
		mBitmap = null;
	}

	// When the layout is calculated, set the 
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		mThisWidth = right - left;
		mThisHeight = bottom - top;
		Runnable r = mOnLayoutRunnable;
		if (r != null) {
			mOnLayoutRunnable = null;
			r.run();
		}
		//		if (mBitmap != null) {
		//			setBaseMatrix(mBitmap, mBaseMatrix);
		//			setImageMatrix(getImageViewMatrix());
		//		}
	}

	// Translate a given point through a given matrix.
	static protected void translatePoint(Matrix matrix, float [] xy) {
		matrix.mapPoints(xy);
	}

	// Identical to the setImageMatrix method in ImageView
	public void setImageMatrix(Matrix m){
		if (m != null && m.isIdentity()) {
			m = null;
		}

		// don't invalidate unless we're actually changing our matrix
		if (m == null && !this.mMatrix.isIdentity() || m != null && !this.mMatrix.equals(m)) {
			this.mMatrix.set(m);
			invalidate();
		}
	}

	// Sets the bitmap for the image and resets the base
	public void setImageBitmap(final Bitmap bitmap) {
		setImageBitmap(bitmap, true);
	}

	// Sets the bitmap for the image and resets the base
	@SuppressLint("NewApi")
	public void setImageBitmap(final Bitmap bitmap, final boolean fitScreen) {
		
		//版本过低或者长度大于最大纹理限制，不采用硬件加速
		if (Build.VERSION.SDK_INT >= MIN_SDK_ENABLE_LAYER_TYPE_HARDWARE) {
			if (bitmap != null && (bitmap.getHeight() > SampleSizeUtil.getTextureSize()
					|| bitmap.getWidth() > SampleSizeUtil.getTextureSize())) {
				setLayerType(View.LAYER_TYPE_SOFTWARE, null);
			} else {
				setLayerType(View.LAYER_TYPE_HARDWARE, null);
			}
		}  
		
		final int viewWidth = getWidth();
		if (viewWidth <= 0)  {
			mOnLayoutRunnable = new Runnable() {
				public void run() {
					setImageBitmap(bitmap, fitScreen);
				}
			};
			return;
		}

		Bitmap oldBitmap = this.mBitmap;
		if (bitmap != null) {
			setBaseMatrix(bitmap, mBaseMatrix);
			this.mBitmap = bitmap;
		} else {
			mBaseMatrix.reset();
			this.mBitmap = bitmap;
		}
		if (oldBitmap != null && oldBitmap != mBitmap && !oldBitmap.isRecycled()) {
			oldBitmap.recycle();
		}

		mSuppMatrix.reset();
		setImageMatrix(getImageViewMatrix());
		mMaxZoom = maxZoom();

		// Set the image to fit the screen
		if(fitScreen) {
			zoomToScreen();
		}
	}

	/**
	 * 
	 * Sets the bitmap for the image and resets the base
	 * @date 2014-4-29
	 * @param bitmap
	 * @param selection
	 */
	public void setImageBitmap(final Bitmap bitmap, final Rect selection ) {
		final int viewWidth = getWidth();



		if (viewWidth <= 0)  {
			mOnLayoutRunnable = new Runnable() {
				public void run() {
					setImageBitmap(bitmap, updateSelection());
				}
			};
			return;
		}

		Bitmap oldBitmap = this.mBitmap;
		if (bitmap != null) {
			setBaseMatrix(bitmap, mBaseMatrix,selection );
			this.mBitmap = bitmap;
		} else {
			mBaseMatrix.reset();
			this.mBitmap = bitmap;
		}
		if (oldBitmap != null && !oldBitmap.isRecycled()) {
			oldBitmap.recycle();
		}

		mSuppMatrix.reset();
		setImageMatrix(getImageViewMatrix());
		mMaxZoom = maxZoom();


	}



	// Unchanged from ImageViewTouchBase
	// Center as much as possible in one or both axis.  Centering is
	// defined as follows:  if the image is scaled down below the 
	// view's dimensions then center it (literally).  If the image
	// is scaled larger than the view and is translated out of view
	// then translate it back into view (i.e. eliminate black bars).
	protected void center(boolean vertical, boolean horizontal, boolean animate) {
		if (mBitmap == null)
			return;

		Matrix m = getImageViewMatrix();

		float [] topLeft  = new float[] { 0, 0 };
		float [] botRight = new float[] { mBitmap.getWidth(), mBitmap.getHeight() };

		translatePoint(m, topLeft);
		translatePoint(m, botRight);

		float height = botRight[1] - topLeft[1];
		float width  = botRight[0] - topLeft[0];

		float deltaX = 0, deltaY = 0;

		if (vertical) {
			int viewHeight = getHeight();
			if (height < viewHeight) {
				deltaY = (viewHeight - height)/2 - topLeft[1];
			} else if (topLeft[1] > 0) {
				deltaY = -topLeft[1];
			} else if (botRight[1] < viewHeight) {
				deltaY = getHeight() - botRight[1];
			}
		}

		if (horizontal) {
			int viewWidth = getWidth();
			if (width < viewWidth) {
				deltaX = (viewWidth - width)/2 - topLeft[0];
			} else if (topLeft[0] > 0) {
				deltaX = -topLeft[0];
			} else if (botRight[0] < viewWidth) {
				deltaX = viewWidth - botRight[0];
			}
		}

		postTranslate(deltaX, deltaY);
		if (animate) {
			Animation a = new TranslateAnimation(-deltaX, 0, -deltaY, 0);
			a.setStartTime(SystemClock.elapsedRealtime());
			a.setDuration(250);
			setAnimation(a);
		}
		setImageMatrix(getImageViewMatrix());
	}

	// Unchanged from ImageViewTouchBase
	protected float getValue(Matrix matrix, int whichValue) {
		matrix.getValues(mMatrixValues);
		return mMatrixValues[whichValue];
	}

	// Get the scale factor out of the matrix.
	protected float getScale(Matrix matrix) {

		// If the bitmap is set return the scale
		if(mBitmap!=null)
			return getValue(matrix, Matrix.MSCALE_X);
		// Otherwise return the default value of 1
		else
			return 1f;
	}

	// Returns the current scale of the view 
	public float getScale() {
		return getScale(mSuppMatrix);
	}

	// Setup the base matrix so that the image is centered and scaled properly.
	private void setBaseMatrix(Bitmap bitmap, Matrix matrix) {
		float viewWidth = getWidth();
		float viewHeight = getHeight();

		matrix.reset();
		float widthScale = Math.min(viewWidth / (float)bitmap.getWidth(), 1.0f);
		float heightScale = Math.min(viewHeight / (float)bitmap.getHeight(), 1.0f);
		float scale;
		if (widthScale > heightScale) {
			scale = heightScale;
		} else {
			scale = widthScale;
		}
		matrix.setScale(scale, scale);
		matrix.postTranslate(
				(viewWidth  - ((float)bitmap.getWidth()  * scale))/2F, 
				(viewHeight - ((float)bitmap.getHeight() * scale))/2F);
	}


	/**
	 *  Setup the base matrix so that the image is centered and scaled properly.
	 * 根据Bitmap和Rect，设置初始的显示矩阵Matrix
	 * @author Linleja
	 * @date 2014-4-29
	 * @param bitmap
	 * @param matrix
	 * @param selection
	 */
	private void setBaseMatrix(Bitmap bitmap, Matrix matrix, Rect selection) {
		if(selection == null){
			return ;
		}

		float viewWidth = selection.right - selection.left;
		float viewHeight = selection.bottom - selection.top;

		matrix.reset();



		float widthRatio = viewWidth / (float)bitmap.getWidth();
		float heighRatio = viewHeight / (float)bitmap.getHeight();
		float scale = 1.0f;
		if (widthRatio > heighRatio) {
			scale = widthRatio;


		} else {
			scale = heighRatio;


		}

		matrix.setScale(scale, scale);
		matrix.postTranslate(
				((getWidth()  - (float)bitmap.getWidth()  * scale))/2F, 
				((getHeight() - (float)bitmap.getHeight() * scale))/2F);
	}


	// Combine the base matrix and the supp matrix to make the final matrix.
	protected Matrix getImageViewMatrix() {
		mDisplayMatrix.set(mBaseMatrix);
		mDisplayMatrix.postConcat(mSuppMatrix);
		return mDisplayMatrix;
	}

	// Sets the maximum zoom, which is a scale relative to the base matrix. It is calculated to show
	// the image at 400% zoom regardless of screen or image orientation. If in the future we decode
	// the full 3 megapixel image, rather than the current 1024x768, this should be changed down to
	// 200%.
	protected float maxZoom() {
		if (mBitmap == null)
			return 1F;

		float fw = (float) mBitmap.getWidth()  / (float)mThisWidth;
		float fh = (float) mBitmap.getHeight() / (float)mThisHeight;
		float max = Math.max(fw, fh) * 16;


		//设置放大的下限
		if(max < 1F){
			max = 1F;
		}


		return max;
	}

	// Tries to make best use of the space by zooming the picture
	public float zoomDefault() {
		if (mBitmap == null)
			return 1F;

		float fw = (float)mThisWidth/(float)mBitmap.getWidth();
		float fh = (float)mThisHeight/(float)mBitmap.getHeight();
		return Math.max(Math.min(fw, fh),1);
	}

	// Unchanged from ImageViewTouchBase
	protected void zoomTo(float scale, float centerX, float centerY) {
		if (scale > mMaxZoom) {
			scale = mMaxZoom;
		}

		float oldScale = getScale();
		float deltaScale = scale / oldScale;

		mSuppMatrix.postScale(deltaScale, deltaScale, centerX, centerY);
		setImageMatrix(getImageViewMatrix());
		center(true, true, false);
	}

	// Unchanged from ImageViewTouchBase
	protected void zoomTo(final float scale, final float centerX, final float centerY, final float durationMs) {
		final float incrementPerMs = (scale - getScale()) / durationMs;
		final float oldScale = getScale();
		final long startTime = System.currentTimeMillis();

		// Setup the zoom runnable
		post(new Runnable() {
			public void run() {
				long now = System.currentTimeMillis();
				float currentMs = Math.min(durationMs, (float)(now - startTime));
				float target = oldScale + (incrementPerMs * currentMs);
				zoomTo(target, centerX, centerY);

				if (currentMs < durationMs) {
					post(this);
				}
			}
		});
	}

    private boolean adjustLongImageEnable = true;
    public void setAdjustLongImageEnable(boolean enable) {
        this.adjustLongImageEnable = enable;
    }

	public void zoomToScreen() {
		if (mBitmap == null)
			return;

		float scale = 1f;
		float fw = (float)mThisWidth/(float)mBitmap.getWidth();

        boolean needAdjust = false;
        if (adjustLongImageEnable) {
            //长>>>宽 比如长微博 横向撑满屏幕，上下滑动浏览全文
            if ((float)mBitmap.getHeight() / (float)mBitmap.getWidth() > MAX_IMAGE_RATIO_LARGE) {
                needAdjust = true;
                scale = fw;
            }
            else if (landscape && (float)mBitmap.getHeight() / (float)mBitmap.getWidth() > MAX_IMAGE_RATIO_WIDTH_LARGE_LANDSCAPE) {
                needAdjust = true;
                scale = fw;
            }
        }

        if (needAdjust) {
            float oldScale = getScale();
            float deltaScale = scale / oldScale;

            mBaseMatrix.reset();
            mSuppMatrix.postScale(deltaScale, deltaScale, 0, 0);
            setImageMatrix(getImageViewMatrix());
        }
		else {
			zoomTo(zoomDefault());
		}
	}

	// Unchanged from ImageViewTouchBase
	public void zoomTo(float scale) {
		float width = getWidth();
		float height = getHeight();

		zoomTo(scale, width/2F, height/2F);
	}

	// Unchanged from ImageViewTouchBase
	public void zoomIn() {
		zoomIn(sScaleRate);
	}

	// Unchanged from ImageViewTouchBase
	public void zoomOut() {
		zoomOut(sScaleRate);
	}

	// Unchanged from ImageViewTouchBase
	protected void zoomIn(float rate) {
		if (getScale() >= mMaxZoom) {
			return;     // Don't let the user zoom into the molecular level.
		}
		if (mBitmap == null) {
			return;
		}

		float width = getWidth();
		float height = getHeight();

		mSuppMatrix.postScale(rate, rate, width/2F, height/2F);
		setImageMatrix(getImageViewMatrix());

	}

	// Unchanged from ImageViewTouchBase
	protected void zoomOut(float rate) {
		if (mBitmap == null) {
			return;
		}

		float width = getWidth();
		float height = getHeight();

		Matrix tmp = new Matrix(mSuppMatrix);
		tmp.postScale(1F/sScaleRate, 1F/sScaleRate, width/2F, height/2F);
		if (getScale(tmp) < 1F) {
			mSuppMatrix.setScale(1F, 1F, width/2F, height/2F);
		} else {
			mSuppMatrix.postScale(1F/rate, 1F/rate, width/2F, height/2F);
		}
		setImageMatrix(getImageViewMatrix());
		center(true, true, false);

	}

	// Unchanged from ImageViewTouchBase
	protected boolean postTranslate(float dx, float dy) {
		return mSuppMatrix.postTranslate(dx, dy);
	}

	// Fling a view by a distance over time
	protected void scrollBy( float distanceX, float distanceY, final float durationMs ) {
		final float dx = distanceX;
		final float dy = distanceY;
		final long startTime = System.currentTimeMillis();

		mFling = new Runnable() {
			float old_x	= 0;
			float old_y	= 0;

			public void run()
			{
				long now = System.currentTimeMillis();
				float currentMs = Math.min( durationMs, now - startTime );
				float x = easeOut( currentMs, 0, dx, durationMs );
				float y = easeOut( currentMs, 0, dy, durationMs );
				postTranslate( ( x - old_x ), ( y - old_y ) );
				center(true, true, false);

				old_x = x;
				old_y = y;
				if ( currentMs < durationMs ) {
					fling = post( this );
				} else {
					stopFling();
				}
			}
		};

		fling = post( mFling );
	}

	protected void stopFling() {
		removeCallbacks(mFling);
		if (fling) {
			fling = false;
			onScrollFinish();
		}
	}

	protected boolean fling() {
		return fling;
	}

	// Gradually slows down a fling velocity
	private float easeOut( float time, float start, float end, float duration){
		return end * ( ( time = time / duration - 1 ) * time * time + 1 ) + start;
	}

	protected void onScrollFinish() {

	}

	// Custom draw operation to draw the bitmap using mMatrix
	@SuppressLint("NewApi")
	@Override
	protected void onDraw(Canvas canvas) {
		// Check if the bitmap was ever set
		if(mBitmap!=null && !mBitmap.isRecycled() ){

			// If the current version is above Gingerbread and the layer type is 
			// hardware accelerated, the paint is no longer needed
			if( Build.VERSION.SDK_INT >=  MIN_SDK_ENABLE_LAYER_TYPE_HARDWARE
					&& getLayerType() == View.LAYER_TYPE_HARDWARE ){
				canvas.drawBitmap(mBitmap, mMatrix, null);
			} 
			else 
			{
				// Check if the time between draws has been met and draw the bitmap
				if( (System.currentTimeMillis()-mLastDraw) > sPaintDelay ){
					canvas.drawBitmap(mBitmap, mMatrix, mPaint);
					mLastDraw = System.currentTimeMillis();
				}

				// Otherwise draw the bitmap without the paint and resubmit a new request
				else{
					canvas.drawBitmap(mBitmap, mMatrix, null);
					removeCallbacks(mRefresh);
					postDelayed(mRefresh, sPaintDelay);
				}
			}			
		}
	}

	protected boolean isScrollOver(float distanceX) {
		try {
			if (mDisplayMatrix != null) {
				float m_x = getValue(mDisplayMatrix, Matrix.MTRANS_X); //图片的左边离屏宽度
				float width = getWidth() - m_x; 
				//width 代表 屏幕宽度+左边离屏宽度 
				//mBitmap.getWidth() * getValue(mDisplayMatrix, Matrix.MSCALE_X) 代表 当前图片显示宽度
				//width == mBitmap.getWidth() * getValue(mDisplayMatrix, Matrix.MSCALE_X) 意味着图片右边离屏宽度 == 0，已经滑到最右边								
				if ((m_x == 0 && distanceX <= 0) //到达图片的最左边继续往左边滑
						|| (width == mBitmap.getWidth() //到达图片的最右边继续往右边滑
						* getValue(mDisplayMatrix, Matrix.MSCALE_X) && distanceX >= 0)) {
					System.out.println("ScrollOver");
					return true;
				}
			}
		}
		catch (IllegalArgumentException e) {
			Log.v("Vincent", "isScrollOver");
			e.printStackTrace();  
		}  

		return false;
	}


	protected Rect updateSelection() {
		return null;
	}

}
