package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.high_order_func

import androidx.viewpager.widget.ViewPager

/**
 * Created by fancy on 2017/7/20.
 * Copyright © 2017 O2. All rights reserved.
 */

class _OnPageChangeListener : ViewPager.OnPageChangeListener {

    private var _onPageScrollStateChanged: ((state: Int) -> Unit)? = null
    private var _onPageScrolled: ((position: Int, positionOffset: Float, positionOffsetPixels: Int) -> Unit)? = null
    private var _onPageSelected: ((position: Int) -> Unit)? = null


    override fun onPageScrollStateChanged(state: Int) {
        _onPageScrollStateChanged?.invoke(state)
    }
    fun onPageScrollStateChanged(func: (state: Int) -> Unit) {
        _onPageScrollStateChanged = func
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        _onPageScrolled?.invoke(position, positionOffset, positionOffsetPixels)
    }
    fun onPageScrolled(func: (position: Int, positionOffset: Float, positionOffsetPixels: Int) -> Unit) {
        _onPageScrolled = func
    }

    override fun onPageSelected(position: Int) {
        _onPageSelected?.invoke(position)
    }
    fun onPageSelected(func: (position: Int) -> Unit) {
        _onPageSelected = func
    }
}