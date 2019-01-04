package net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.support.annotation.ColorInt
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.Interpolator
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog

/**
 * Created by fancyLou on 09/05/2018.
 * Copyright © 2018 O2. All rights reserved.
 */


class CircleRippleView : View {

    private val MAXRADIUSRATE = 0.75
    private val mDuration: Long = 2000
    private val mSpeed: Long = 500
    private val mPaint: Paint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }
    private val mCircleList: ArrayList<Circle> = arrayListOf<Circle>()

    //auto set
    private var mMaxRadius: Float = 0.0f
    private var mInitialRadius: Float = 0.0f
    private var mIsRunning = false
    private var mIcon: Bitmap? = null
    private var mScaleIcon : Bitmap? = null

    //to set
    var mInterpolator = AccelerateInterpolator(1.2f)


    constructor(context: Context) : super(context) {
        init(context, null, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr)
    }


    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        try {
            mIcon = getVoiceBitmapFromVectorDrawable(context)
        } catch (e: Exception) {}

        mCreateCircle = Runnable {
            if (mIsRunning) {
                newCircle()
                if (mCreateCircle!=null) {
                    postDelayed(mCreateCircle, mSpeed)
                }
            }
        }
        mPaint.style = Paint.Style.FILL
        mPaint.color = ContextCompat.getColor(context, R.color.z_color_primary)
    }

    /**
     * 设置画笔颜色
     */
    fun setPaintColor(@ColorInt color: Int) {
        mPaint.color = color
    }

    /**
     * 设置画笔样式
     */
    fun setPaintStyle(style: Paint.Style) {
        mPaint.style = style
    }


    /**
     * 开始
     */
    fun start() {
        XLog.info("start.....$mIsRunning")
        if (!mIsRunning) {
            mIsRunning = true
            post(mCreateCircle)
        }
    }

    /**
     * 缓慢停止
     */
    fun stop() {
        mIsRunning = false
//        removeCallbacks(mCreateCircle)
    }

    /**
     * 立即停止
     */
    fun stopImmediately() {
        mIsRunning = false
        mCircleList.clear()
//        removeCallbacks(mCreateCircle)
        invalidate()
    }

    fun isRunning(): Boolean {
        return mIsRunning
    }


    override fun onDraw(canvas: Canvas?) {
        val iterator = mCircleList.iterator()
        while (iterator.hasNext()) {
            val next = iterator.next()
            if (System.currentTimeMillis() - next.mCreateTime < mDuration) {
                val alpha = next.getAlpha()
                mPaint.alpha = alpha
                canvas?.drawCircle(width / 2.toFloat(), height / 2.toFloat(), next.getCurrentRadius(), mPaint)
            } else {
                iterator.remove()
            }
        }
        if (mCircleList.size > 0) {
            postInvalidateDelayed(10)
        }
        if (mIcon!=null) {

            if(mScaleIcon==null) {
                mScaleIcon = Bitmap.createScaledBitmap(mIcon, (2 * mInitialRadius * MAXRADIUSRATE).toInt(), (2 * mInitialRadius * MAXRADIUSRATE).toInt(), false)
            }
            val left = (width/2 - mInitialRadius * MAXRADIUSRATE).toFloat()
            val top = (height/2 - mInitialRadius * MAXRADIUSRATE).toFloat()
            canvas?.drawBitmap(mScaleIcon, left, top, null)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        mMaxRadius = (Math.min(w, h) /2).toFloat()
        mInitialRadius = mMaxRadius / 4
    }

    private var mCreateCircle: Runnable? = null

    private fun newCircle() {
        mCircleList.add(Circle(mDuration, mMaxRadius, mInitialRadius, mInterpolator))
        invalidate()
    }

    private fun getVoiceBitmapFromVectorDrawable(context: Context): Bitmap {
        var drawable = ContextCompat.getDrawable(context, R.drawable.voice_vector)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = DrawableCompat.wrap(drawable).mutate()
        }
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }



    class Circle(private val mDuration: Long, private val mMaxRadius: Float,
                 private val mInitialRadius: Float, private val interpolator: Interpolator) {
        val mCreateTime = System.currentTimeMillis()

        fun getAlpha(): Int {
            val percent = (System.currentTimeMillis() - mCreateTime) * 1.0f / mDuration
            return ((1.0 - interpolator.getInterpolation(percent)) * 255).toInt()
        }

        fun getCurrentRadius(): Float {
            val percent = (System.currentTimeMillis() - mCreateTime) * 1.0f / mDuration
            return mInitialRadius + (interpolator.getInterpolation(percent) * (mMaxRadius - mInitialRadius))
        }

    }

}