/*
 * Copyright (C) 2014 The Android Open Source Project
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

package net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.support.annotation.ColorRes;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.animation.Animation;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;


/**
 * Private class created to work around issues with AnimationListeners being
 * called before the animation is actually complete and support shadows on older
 * platforms.
 */
public class CircleProgressBar extends android.support.v7.widget.AppCompatImageView {

    private static final int KEY_SHADOW_COLOR = 0x1E000000;
    private static final int FILL_SHADOW_COLOR = 0x3D000000;
    // PX
    private static final float X_OFFSET = 0f;
    private static final float Y_OFFSET = 1.75f;
    private static final float SHADOW_RADIUS = 3.5f;
    private static final int SHADOW_ELEVATION = 4;


    private static final int DEFAULT_CIRCLE_BG_LIGHT = 0xFFFAFAFA;
    private static final int DEFAULT_CIRCLE_DIAMETER = 56;
    private static final int STROKE_WIDTH_LARGE = 3;
    public static final int DEFAULT_TEXT_SIZE = 9;

    private Animation.AnimationListener mListener;
    private int mShadowRadius;
    private int mBackGroundColor;
    private int mProgressColor;
    private int mProgressStokeWidth;
    private int mArrowWidth;
    private int mArrowHeight;
    private int mProgress;
    private int mMax;
    private int mDiameter;
    private int mInnerRadius;
    private Paint mTextPaint;
    private int mTextColor;
    private int mTextSize;
    private boolean mIfDrawText;
    private boolean mShowArrow;
    private MaterialProgressDrawable mProgressDrawable;
    private ShapeDrawable mBgCircle;
    private boolean mCircleBackgroundEnabled;
    private int[] mColors = new int[]{Color.BLACK};

    public CircleProgressBar(Context context) {
        super(context);
        init(context, null, 0);

    }

    public CircleProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);

    }

    public CircleProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.CircleProgressBar, defStyleAttr, 0);

        final float density = getContext().getResources().getDisplayMetrics().density;

        mBackGroundColor = a.getColor(
                R.styleable.CircleProgressBar_mlpb_background_color, DEFAULT_CIRCLE_BG_LIGHT);

        mProgressColor = a.getColor(
                R.styleable.CircleProgressBar_mlpb_progress_color, DEFAULT_CIRCLE_BG_LIGHT);
        mColors = new int[]{mProgressColor};

        mInnerRadius = a.getDimensionPixelOffset(
                R.styleable.CircleProgressBar_mlpb_inner_radius, -1);

        mProgressStokeWidth = a.getDimensionPixelOffset(
                R.styleable.CircleProgressBar_mlpb_progress_stoke_width, (int) (STROKE_WIDTH_LARGE * density));
        mArrowWidth = a.getDimensionPixelOffset(
                R.styleable.CircleProgressBar_mlpb_arrow_width, -1);
        mArrowHeight = a.getDimensionPixelOffset(
                R.styleable.CircleProgressBar_mlpb_arrow_height, -1);
        mTextSize = a.getDimensionPixelOffset(
                R.styleable.CircleProgressBar_mlpb_progress_text_size, (int) (DEFAULT_TEXT_SIZE * density));
        mTextColor = a.getColor(
                R.styleable.CircleProgressBar_mlpb_progress_text_color, Color.BLACK);

        mShowArrow = a.getBoolean(R.styleable.CircleProgressBar_mlpb_show_arrow, false);
        mCircleBackgroundEnabled = a.getBoolean(R.styleable.CircleProgressBar_mlpb_enable_circle_background, true);


        mProgress = a.getInt(R.styleable.CircleProgressBar_mlpb_progress, 0);
        mMax = a.getInt(R.styleable.CircleProgressBar_mlpb_max, 100);
        int textVisible = a.getInt(R.styleable.CircleProgressBar_mlpb_progress_text_visibility, 1);
        if (textVisible != 1) {
            mIfDrawText = true;
        }

        mTextPaint = new Paint();
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setAntiAlias(true);
        a.recycle();
        mProgressDrawable = new MaterialProgressDrawable(getContext(), this);
        super.setImageDrawable(mProgressDrawable);
    }


    private boolean elevationSupported() {
        return android.os.Build.VERSION.SDK_INT >= 21;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!elevationSupported()) {
            setMeasuredDimension(getMeasuredWidth() + mShadowRadius * 2, getMeasuredHeight()
                    + mShadowRadius * 2);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        final float density = getContext().getResources().getDisplayMetrics().density;
        mDiameter = Math.min(getMeasuredWidth(), getMeasuredHeight());
        if (mDiameter <= 0) {
            mDiameter = (int) density * DEFAULT_CIRCLE_DIAMETER;
        }
        if (getBackground() == null && mCircleBackgroundEnabled) {
            final int shadowYOffset = (int) (density * Y_OFFSET);
            final int shadowXOffset = (int) (density * X_OFFSET);
            mShadowRadius = (int) (density * SHADOW_RADIUS);

            if (elevationSupported()) {
                mBgCircle = new ShapeDrawable(new OvalShape());
                ViewCompat.setElevation(this, SHADOW_ELEVATION * density);
            } else {
                OvalShape oval = new OvalShadow(mShadowRadius, mDiameter - mShadowRadius * 2);
                mBgCircle = new ShapeDrawable(oval);
                ViewCompat.setLayerType(this, ViewCompat.LAYER_TYPE_SOFTWARE, mBgCircle.getPaint());
                mBgCircle.getPaint().setShadowLayer(mShadowRadius, shadowXOffset, shadowYOffset,
                        KEY_SHADOW_COLOR);
                final int padding = (int) mShadowRadius;
                // set padding so the inner image sits correctly within the shadow.
                setPadding(padding, padding, padding, padding);
            }
            mBgCircle.getPaint().setColor(mBackGroundColor);
            setBackgroundDrawable(mBgCircle);
        }
        mProgressDrawable.setBackgroundColor(mBackGroundColor);
        mProgressDrawable.setColorSchemeColors(mColors);
        mProgressDrawable.setSizeParameters(mDiameter, mDiameter,
                mInnerRadius <= 0 ? (mDiameter - mProgressStokeWidth * 2) / 4 : mInnerRadius,
                mProgressStokeWidth,
                mArrowWidth < 0 ? mProgressStokeWidth * 4 : mArrowWidth,
                mArrowHeight < 0 ? mProgressStokeWidth * 2 : mArrowHeight);
        if (isShowArrow()) {
            mProgressDrawable.showArrowOnFirstStart(true);
            mProgressDrawable.setArrowScale(1f);
            mProgressDrawable.showArrow(true);
        }
        super.setImageDrawable(null);
        super.setImageDrawable(mProgressDrawable);
        mProgressDrawable.setAlpha(255);
        if(getVisibility()==VISIBLE) {
            mProgressDrawable.start();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mIfDrawText) {
            String text = String.format("%s%%", mProgress);
            int x = getWidth() / 2 - text.length() * mTextSize / 4;
            int y = getHeight() / 2 + mTextSize / 4;
            canvas.drawText(text, x, y, mTextPaint);
        }
    }

    @Override
    final public void setImageResource(int resId) {

    }


    public boolean isShowArrow() {
        return mShowArrow;
    }

    public void setShowArrow(boolean showArrow) {
        this.mShowArrow = showArrow;
    }


    @Override
    final public void setImageURI(Uri uri) {
        super.setImageURI(uri);
    }

    @Override
    final public void setImageDrawable(Drawable drawable) {
    }

    public void setAnimationListener(Animation.AnimationListener listener) {
        mListener = listener;
    }

    @Override
    public void onAnimationStart() {
        super.onAnimationStart();
        if (mListener != null) {
            mListener.onAnimationStart(getAnimation());
        }
    }

    @Override
    public void onAnimationEnd() {
        super.onAnimationEnd();
        if (mListener != null) {
            mListener.onAnimationEnd(getAnimation());
        }
    }


    /**
     * Set the color resources used in the progress animation from color resources.
     * The first color will also be the color of the bar that grows in response
     * to a user swipe gesture.
     *
     * @param colorResIds
     */
    public void setColorSchemeResources(int... colorResIds) {
        final Resources res = getResources();
        int[] colorRes = new int[colorResIds.length];
        for (int i = 0; i < colorResIds.length; i++) {
            colorRes[i] = res.getColor(colorResIds[i]);
        }
        setColorSchemeColors(colorRes);
    }

    /**
     * Set the colors used in the progress animation. The first
     * color will also be the color of the bar that grows in response to a user
     * swipe gesture.
     *
     * @param colors
     */
    public void setColorSchemeColors(int... colors) {
        mColors = colors;
        if (mProgressDrawable != null) {
            mProgressDrawable.setColorSchemeColors(colors);
        }
    }

    /**
     * Update the background color of the mBgCircle image view.
     */
    public void setBackgroundColor(@ColorRes int colorRes) {
        if (getBackground() instanceof ShapeDrawable) {
            final Resources res = getResources();
            ((ShapeDrawable) getBackground()).getPaint().setColor(res.getColor(colorRes));
        }
    }

    public boolean isShowProgressText() {
        return mIfDrawText;
    }

    public void setShowProgressText(boolean mIfDrawText) {
        this.mIfDrawText = mIfDrawText;
    }

    public int getMax() {
        return mMax;
    }

    public void setMax(int max) {
        mMax = max;
    }

    public int getProgress() {
        return mProgress;
    }

    public void setProgress(int progress) {
        if (getMax() > 0) {
            mProgress = progress;
        }
    }


    public boolean circleBackgroundEnabled() {
        return mCircleBackgroundEnabled;
    }

    public void setCircleBackgroundEnabled(boolean enableCircleBackground) {
        this.mCircleBackgroundEnabled = enableCircleBackground;
    }

    @Override
    public int getVisibility() {
        return super.getVisibility();
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (mProgressDrawable != null) {
            mProgressDrawable.setVisible(visibility == VISIBLE, false);
            if (visibility != VISIBLE) {
                mProgressDrawable.stop();
            } else {
                if (mProgressDrawable.isRunning()) {
                    mProgressDrawable.stop();
                }
                mProgressDrawable.start();
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mProgressDrawable != null) {
            mProgressDrawable.stop();
            mProgressDrawable.setVisible(getVisibility() == VISIBLE, false);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mProgressDrawable != null) {
            mProgressDrawable.stop();
            mProgressDrawable.setVisible(false, false);
        }
    }


    private class OvalShadow extends OvalShape {
        private RadialGradient mRadialGradient;
        private int mShadowRadius;
        private Paint mShadowPaint;
        private int mCircleDiameter;

        public OvalShadow(int shadowRadius, int circleDiameter) {
            super();
            mShadowPaint = new Paint();
            mShadowRadius = shadowRadius;
            mCircleDiameter = circleDiameter;
            mRadialGradient = new RadialGradient(mCircleDiameter / 2, mCircleDiameter / 2,
                    mShadowRadius, new int[]{
                    FILL_SHADOW_COLOR, Color.TRANSPARENT
            }, null, Shader.TileMode.CLAMP);
            mShadowPaint.setShader(mRadialGradient);
        }

        @Override
        public void draw(Canvas canvas, Paint paint) {
            final int viewWidth = CircleProgressBar.this.getWidth();
            final int viewHeight = CircleProgressBar.this.getHeight();
            canvas.drawCircle(viewWidth / 2, viewHeight / 2, (mCircleDiameter / 2 + mShadowRadius),
                    mShadowPaint);
            canvas.drawCircle(viewWidth / 2, viewHeight / 2, (mCircleDiameter / 2), paint);
        }
    }
}
