package net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent

/**
 * Created by fancyLou on 2018/1/25.
 * Copyright © 2018 O2. All rights reserved.
 */


class NoScollViewPager : android.support.v4.view.ViewPager {

    var canSlide: Boolean = false

    constructor(context: Context): super(context)
    constructor(context: Context, attr: AttributeSet): super(context, attr)


    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return canSlide && super.onTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return canSlide && super.onInterceptTouchEvent(ev)
    }
}