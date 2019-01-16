package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils

import android.content.Context
import android.support.v4.widget.SwipeRefreshLayout
import org.jetbrains.anko.dip

/**
 * Created by fancyLou on 2017/12/8.
 * Copyright © 2017 O2. All rights reserved.
 */

object MiscUtilK {
    /**
     * 初始化页面的时候启动加载动画
     * @param refreshLayout
     * @param context
     */
    fun swipeRefreshLayoutRun(refreshLayout: SwipeRefreshLayout, context: Context) {
        refreshLayout.setProgressViewOffset(false, 0, context.dip(24f))
        refreshLayout.isRefreshing = true
    }
}