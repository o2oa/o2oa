package net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Build
import android.support.annotation.ColorInt
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatButton
import android.util.AttributeSet
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R

/**
 * 带进度条按钮
 * Created by fancyLou on 2017/12/19.
 * Copyright © 2017 O2. All rights reserved.
 */

class O2ProgressButton : AppCompatButton {

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        init(context, attributeSet)
    }

    constructor(context: Context, attributeSet: AttributeSet, def: Int) : super(context, attributeSet, def) {
        init(context, attributeSet)
    }

    private lateinit var mButtonDrawable: StateListDrawable
    private val mProgressDrawable: GradientDrawable by lazy { resources.getDrawable(R.drawable.o2_progress_button_foreground).mutate() as GradientDrawable }
    private val mButtonBackgroundDrawable: GradientDrawable by lazy { resources.getDrawable(R.drawable.o2_progress_button_background).mutate() as GradientDrawable }

    private var mCornerRadius: Float = 0f
    private var mProgressColor: Int = -1
    private var mProgress: Int = 0 //当前进度
    private val mMaxProgress = 100 //最大进度：默认为100
    private val mMinProgress = 0//最小进度：默认为0
    private var isFinish = false
    private var mFinishListener: ProgressFinishListener? = null

    private fun init(context: Context, attributeSet: AttributeSet?) {
        mButtonDrawable = StateListDrawable()
        val attr = context.obtainStyledAttributes(attributeSet, R.styleable.O2ProgressButton)
        try {
            mCornerRadius = attr.getDimension(R.styleable.O2ProgressButton_o2CornerRadius, (8 * resources.displayMetrics.density))
            mProgressColor = attr.getColor(R.styleable.O2ProgressButton_o2ButtonColor, ContextCompat.getColor(context, R.color.z_color_primary))
            mProgressDrawable.setColor(mProgressColor)
            mProgressDrawable.cornerRadius = mCornerRadius
            mButtonBackgroundDrawable.cornerRadius = mCornerRadius

            mButtonDrawable.addState(intArrayOf(-android.R.attr.state_enabled), mButtonBackgroundDrawable)
            mButtonDrawable.addState(intArrayOf(), mProgressDrawable)
        } catch (e: Exception) {
        } finally {
            attr.recycle()
        }
        isFinish = false
        setBackgroundCompat(mButtonDrawable)
    }

    override fun onDraw(canvas: Canvas?) {
        if (mProgress in (mMinProgress + 1)..mMaxProgress && !isFinish) {
            val scale = (mProgress.toFloat() / mMaxProgress.toFloat())
            val nowWidth = measuredWidth.toFloat() * scale
            mButtonDrawable.setBounds(0, 0, nowWidth.toInt(), measuredHeight)
            mButtonDrawable.draw(canvas)
            if (mProgress == mMaxProgress) {
                setBackgroundCompat(mButtonDrawable)
                isFinish = true
                mFinishListener?.onFinish()
            }
        }
        super.onDraw(canvas)
    }

    /**
     * 换肤使用 变按钮颜色
     */
    fun changeColor(@ColorInt color: Int) {
        mProgressColor = color
        mProgressDrawable.setColor(mProgressColor)
        mButtonDrawable = StateListDrawable()
        mButtonDrawable.addState(intArrayOf(-android.R.attr.state_enabled), mButtonBackgroundDrawable)
        mButtonDrawable.addState(intArrayOf(), mProgressDrawable)
        setBackgroundCompat(mButtonDrawable)
        invalidate()
    }


    fun setProgress(progress: Int) {
        if (!isFinish) {
            mProgress = progress
            text = "$mProgress %"
            setBackgroundCompat(mButtonBackgroundDrawable)
            invalidate()
        }
    }

    fun addFinishListener(finishListener: ProgressFinishListener) {
        mFinishListener = finishListener
    }


    private fun setBackgroundCompat(drawable: Drawable) {
        val left = paddingLeft
        val right = paddingRight
        val top = paddingTop
        val bottom = paddingBottom
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            background = drawable
        } else {
            setBackgroundDrawable(drawable)
        }
        setPadding(left, top, right, bottom)
    }


    interface ProgressFinishListener {
        fun onFinish()
    }
}