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

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class MultiTouchZoomableImageView extends BaseZoomableImageView {
	// Scale and gesture listeners for the view
	private GestureDetector mGestureDetector;
	private ScaleGestureDetector mScaleDetector;
	protected boolean transIgnoreScale = false;
	private boolean scaleRecognized = false;
	
	// Programatic entry point
	public MultiTouchZoomableImageView(Context context) {
		super(context);
		initMultiTouchZoomableImageView( context );
	}

	// XML entry point
	public MultiTouchZoomableImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initMultiTouchZoomableImageView( context );
	}

	// Setup the view
	protected void initMultiTouchZoomableImageView( Context context) {
		// Setup the gesture and scale listeners
		mScaleDetector = new ScaleGestureDetector( context, new ScaleListener() );
		mGestureDetector = new GestureDetector(context, new MyGestureListener());
	}


	// Adjusts the zoom of the view
	class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

		@Override
		public boolean onScale( ScaleGestureDetector detector )
		{	
			// Check if the detector is in progress in order to proceed
			if(detector!=null && detector.isInProgress() ){
				try{
					// Grab the scale
					float targetScale = getScale() * detector.getScaleFactor();
					// Correct for the min scale
					targetScale = Math.min( maxZoom(), Math.max( targetScale, 1.0f) );

					// Zoom and invalidate the view
					zoomTo( targetScale, detector.getFocusX(), detector.getFocusY() );
					invalidate();

					scaleRecognized = true;
					
					return true;
				}catch(IllegalArgumentException e){
					e.printStackTrace();
				}
			}
			return false;
		}
	}

	// Handles taps and scrolls of the view
	private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
		
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			if(mImageGestureListener!=null){
				mImageGestureListener.onImageGestureSingleTapConfirmed();
				return false;
			}
			
			return super.onSingleTapConfirmed(e);
		}
		
        public void onLongPress(MotionEvent e) {
			if(mImageGestureListener!=null && !scaleRecognized){
				mImageGestureListener.onImageGestureLongPress();
			}			
        }

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			try {
				// Skip if there are multiple points of contact
				if ( (e1!=null&&e1.getPointerCount() > 1) || (e2!=null&&e2.getPointerCount() > 1) || (mScaleDetector!=null && mScaleDetector.isInProgress()) ) 
					return false;

				// Scroll the bitmap
				if (transIgnoreScale || getScale() > zoomDefault()) {
                    stopFling();
					postTranslate(-distanceX, -distanceY);
					
					if (isScrollOver(distanceX)) {
						if (mViewPager!=null) {
							mViewPager.requestDisallowInterceptTouchEvent(false);
						}
					}
					else {
						if (mViewPager!=null) {
							mViewPager.requestDisallowInterceptTouchEvent(true);
						}
					}
					
					center(true, true, false);
				}
				else {
					if (mViewPager!=null) {
						mViewPager.requestDisallowInterceptTouchEvent(false);
					}					
				}
			} 			
			catch (IllegalArgumentException e) {
		        e.printStackTrace();  
		    } 

			// Default case
			return true;
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			// If the zoom is over 1x, reset to 1x
			if ( getScale() != zoomDefault() ){
				zoomTo(zoomDefault());
			}
			// If the zoom is default, zoom into 2x
			else 
				zoomTo(zoomDefault()*3, e.getX(), e.getY(),200);

			// Always true as double tap was performed
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY )
		{
			if ( (e1!=null&&e1.getPointerCount() > 1) || (e2!=null&&e2.getPointerCount() > 1) ) return false;
			if ( mScaleDetector.isInProgress() ) return false;

			final float FLING_MIN_DISTANCE = 100;
			final float FLING_MIN_VELOCITY = 200;
			if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE 
					&& Math.abs(velocityX) > FLING_MIN_VELOCITY) {
				Log.i("MultiTouchZoomableImageView","Fling Left");
			} else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE 
					&& Math.abs(velocityX) > FLING_MIN_VELOCITY) {
				Log.i("MultiTouchZoomableImageView","Fling Right");
			} else if (e1.getY() - e2.getY() > FLING_MIN_DISTANCE 
					&& Math.abs(velocityY) > FLING_MIN_VELOCITY) {
				Log.i("MultiTouchZoomableImageView","Fling Up");
			} else if (e2.getY() - e1.getY() > FLING_MIN_DISTANCE 
					&& Math.abs(velocityY) > FLING_MIN_VELOCITY) {
				Log.i("MultiTouchZoomableImageView","Fling Down");
				
				if (!transIgnoreScale && getScale() <= zoomDefault()) {
					mImageGestureListener.onImageGestureFlingDown();
					return true;	
				}
			} 

			try {
				float diffX = e2.getX() - e1.getX();
				float diffY = e2.getY() - e1.getY();

				if ( Math.abs( velocityX ) > 800 || Math.abs( velocityY ) > 800 ) {
					scrollBy( diffX / 2, diffY / 2, 300 );
					invalidate();
				}
			}
			catch(NullPointerException e){

			}
			catch (IllegalArgumentException e) {
				e.printStackTrace();  
			}  

			return super.onFling( e1, e2, velocityX, velocityY );
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		try {
			if (mViewPager!=null) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_MOVE:
					mViewPager.requestDisallowInterceptTouchEvent(true);
					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_CANCEL:
					mViewPager.requestDisallowInterceptTouchEvent(false);
					scaleRecognized = false;
					break;
				}
			}

			// If the bitmap was set, check the scale and gesture detectors
			if(mBitmap!=null){
				// Check the scale detector
				mScaleDetector.onTouchEvent(event);
				
				// Check the gesture detector
				if(!mScaleDetector.isInProgress())
					mGestureDetector.onTouchEvent(event);
			} else {
				mImageGestureListener.onImageGestureSingleTapConfirmed();
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();  
		}

		return true;
	}
}
