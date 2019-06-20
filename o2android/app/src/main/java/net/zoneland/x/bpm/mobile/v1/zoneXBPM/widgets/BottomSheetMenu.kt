package net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets

import android.animation.Animator
import android.animation.ValueAnimator
import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.borax12.materialdaterangepicker.Utils.dpToPx
import jiguang.chat.pickerimage.utils.ScreenUtil.getBottomStatusHeight
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R

/**
 * Created by fancyLou on 2019/4/19.
 * Copyright © 2019 O2. All rights reserved.
 */
class BottomSheetMenu(private val activity: Activity) {
    private val shadowMax = 0xa0
    private var majorTitle: TextView? = null
    private var title: TextView? = null
    private val viewGroup: FrameLayout
    private val contentGroup: LinearLayout
    private val menuList: LinearLayout
    private val commonMargin = dpToPx(10f, activity.resources)
    private val itemMargin = dpToPx(16f, activity.resources)
    private var contentLayoutHeight = 0

    var isShow = false

    init {
        viewGroup = initViewGroup()
        contentGroup = initContentGroup()
        menuList = initMenuList()
        viewGroup.addView(contentGroup)
        contentGroup.addView(menuList)
    }

    fun show() {
        if (!isShow) {
            (activity.window.decorView as ViewGroup).addView(viewGroup)
            contentGroup.apply {
                post {
                    contentLayoutHeight = measuredHeight
                    translationY = contentLayoutHeight.toFloat()
                    visibility = View.VISIBLE
                    startAnimator(true)
                }
            }
            isShow = true
        }
    }

    fun dismiss() {
        if (isShow) {
            startAnimator(false, object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {

                }

                override fun onAnimationCancel(animation: Animator?) {

                }

                override fun onAnimationStart(animation: Animator?) {

                }

                override fun onAnimationEnd(animation: Animator?) {
                    (activity.window.decorView as ViewGroup).removeView(viewGroup)
                }
            })
            isShow = false
        }
    }

    /**
     * 设置主标题
     */
    fun setMajorTitle(text: String): BottomSheetMenu {
        majorTitle = TextView(activity).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                val margin = dpToPx(20f, activity.resources)
                if (title == null) {
                    setMargins(0, margin, 0, margin)
                } else {
                    setMargins(0, margin, 0, 0)
                }
            }
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setTextColor(Color.parseColor("#8f8f8f"))
            typeface = Typeface.DEFAULT_BOLD
            setText(text)
        }
        menuList.addView(majorTitle, 0)
        return this
    }

    /**
     * 设置副标题
     */
    fun setTitle(text: String): BottomSheetMenu {
        title = TextView(activity).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                val margin = dpToPx(20f, activity.resources)
                if (majorTitle == null) {
                    setMargins(0, margin, 0, margin)
                } else {
                    setMargins(0, 0, 0, margin)
                }
            }
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setTextColor(Color.parseColor("#8f8f8f"))
            setText(text)
        }
        if (majorTitle == null) {
            menuList.addView(title, 0)
        } else {
            menuList.addView(title, 1)
        }
        return this
    }

    /**
     * 设置子项
     */
    fun setItem(text: String, textColor: Int = 0, itemClickListener: () -> Unit): BottomSheetMenu {
        val textView = TextView(activity)
        textView.apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                setPadding(0, itemMargin, 0, itemMargin)
            }
            gravity = Gravity.CENTER
            setBackgroundResource(R.drawable.shape_bottom_list_menu)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            if (textColor == 0) {
                setTextColor(Color.parseColor("#FF3B30"))
            } else {
                try {
                    setTextColor(textColor)
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
            setText(text)
            setOnClickListener {
                itemClickListener.invoke()
                dismiss()
            }
        }
        val lineView = View(activity).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1)
            setBackgroundColor(Color.parseColor("#dcdbdf"))
        }
        menuList.addView(lineView)
        menuList.addView(textView)
        return this
    }

    /**
     * 设置多个子项
     */
    fun setItems(texts: List<String>, textColor: Int = 0, itemClickListener: (Int) -> Unit): BottomSheetMenu {
        if (!texts.isEmpty()) {
            texts.forEachIndexed { index, text ->
                val textView = TextView(activity)
                textView.apply {
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                        setPadding(0, itemMargin, 0, itemMargin)
                    }
                    gravity = Gravity.CENTER
                    setBackgroundResource(R.drawable.shape_bottom_list_menu)
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
                    if (textColor == 0) {
                        setTextColor(Color.parseColor("#FF3B30"))
                    } else {
                        try {
                            setTextColor(textColor)
                        } catch (e: Throwable) {
                            e.printStackTrace()
                        }
                    }
                    setText(text)
                    setOnClickListener {
                        itemClickListener.invoke(index)
                        dismiss()
                    }
                }
                val lineView = View(activity).apply {
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1)
                    setBackgroundColor(Color.parseColor("#dcdbdf"))
                }
                menuList.addView(lineView)
                menuList.addView(textView)
            }
        }

        return this
    }

    /**
     * 设置按钮
     */
    fun setCancelButton(text: String, textColor: Int = 0,  cancelClickListener: () -> Unit): BottomSheetMenu {
        val cancelView = TextView(activity).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                setMargins(commonMargin, 0, commonMargin, commonMargin)
            }
            setPadding(0, itemMargin, 0, itemMargin)
            background = resources.getDrawable(R.drawable.shape_bottom_list_menu)
            if (textColor == 0) {
                setTextColor(Color.parseColor("#FF3B30"))
            } else {
                try {
                    setTextColor(textColor)
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
            gravity = Gravity.CENTER
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            setText(text)
            typeface = Typeface.DEFAULT_BOLD
            setOnClickListener {
                cancelClickListener.invoke()
                dismiss()
            }
        }
        contentGroup.addView(cancelView)
        return this
    }

    /**
     * 初始化容器
     * 背景阴影容器
     */
    private fun initViewGroup() = FrameLayout(activity).apply {
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        setOnClickListener {
            dismiss()
        }
    }

    /**
     * 初始化内容容器
     * 滑动动画载体
     */
    private fun initContentGroup() = LinearLayout(activity).apply {
        layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT).apply {
            gravity = Gravity.BOTTOM
            bottomMargin = getBottomStatusHeight(activity)
            visibility = View.INVISIBLE
        }
        orientation = LinearLayout.VERTICAL
    }

    /**
     * 初始化菜单列表
     */
    private fun initMenuList() = LinearLayout(activity).apply {
        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            setMargins(commonMargin, 0, commonMargin, commonMargin)
        }
        gravity = Gravity.CENTER_HORIZONTAL
        orientation = LinearLayout.VERTICAL
        background = activity.resources.getDrawable(R.drawable.shape_bottom_list_menu)
    }

    private fun startAnimator(enterType: Boolean, listener: Animator.AnimatorListener? = null) {
        viewGroup.post {
            ValueAnimator().apply {
                if (enterType) {
                    setFloatValues(contentLayoutHeight.toFloat(), 0f)
                } else {
                    setFloatValues(0f, contentLayoutHeight.toFloat())
                }
                duration = 300
                interpolator = DecelerateInterpolator()
                addUpdateListener {
                    val value = it.animatedValue as Float
                    contentGroup.translationY = value
                    setShadow()
                }
                listener?.let {
                    addListener(it)
                }
                start()
            }
        }
    }

    private fun setShadow() {
        val ratio = contentGroup.translationY / contentLayoutHeight
        val shadow = (shadowMax * (1 - ratio)).toInt()
        if (shadow >= 16) {
            viewGroup.setBackgroundColor(Color.parseColor("#${shadow.toString(16)}000000"))
        }
    }

}