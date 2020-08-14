package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.high_order_func

import androidx.drawerlayout.widget.DrawerLayout
import android.view.View

/**
 * Created by fancyLou on 28/06/2018.
 * Copyright © 2018 O2. All rights reserved.
 */


class KTXDrawerListener : DrawerLayout.DrawerListener {



    private var _onDrawerStateChanged:((newState: Int)->Unit)? = null
    private var _onDrawerSlide:((drawerView: View?, slideOffset: Float)->Unit)? = null
    private var _onDrawerClosed:((drawerView: View?)->Unit)? = null
    private var _onDrawerOpened:((drawerView: View?)->Unit)? = null

    override fun onDrawerStateChanged(newState: Int) {
        _onDrawerStateChanged?.invoke(newState)
    }

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
        _onDrawerSlide?.invoke(drawerView, slideOffset)
    }


    override fun onDrawerClosed(drawerView: View) {
        _onDrawerClosed?.invoke(drawerView)
    }

    override fun onDrawerOpened(drawerView: View) {
        _onDrawerOpened?.invoke(drawerView)
    }

    fun onDrawerStateChanged(func:(newState: Int)->Unit) {
        _onDrawerStateChanged = func
    }


    fun onDrawerSlide(func:(drawerView: View?, slideOffset: Float)->Unit) {
        _onDrawerSlide = func
    }


    fun onDrawerClosed(func:(drawerView: View?)->Unit) {
        _onDrawerClosed = func
    }


    fun onDrawerOpened(func: (drawerView: View?)->Unit) {
        _onDrawerOpened = func
    }
}