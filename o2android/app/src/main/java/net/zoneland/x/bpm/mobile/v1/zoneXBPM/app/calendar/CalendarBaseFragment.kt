package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.calendar

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2App
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import java.util.*

/**
 * Created by fancyLou on 21/06/2018.
 * Copyright © 2018 O2. All rights reserved.
 */


abstract class CalendarBaseFragment: Fragment() {
    abstract fun layoutResId(): Int
    abstract fun bindViewModel()
    abstract fun initView()


    //业务方法

    /**
     * 公开方法 设置日历过滤条件
     * 显示哪些日历的日程事件
     */
    abstract fun setCalendarFilter(calendarIds: List<String>)

    /**
     * 跳转到今天
     */
    abstract fun jump2Today()

    /**
     * 更新主页面上标题
     * 每个视图显示不一样 月、周、日
     */
    abstract fun updateTitle(cal: Calendar?)

    /**
     * 初始化Fragment的时候获取主页面上日历列表的数据
     */
    abstract fun initCalendarIds(): List<String>

    protected var isSelfShow = false


    protected fun checkManageAble(manageablePersonList: ArrayList<String>): Boolean =
            manageablePersonList.any { it == O2SDKManager.instance().distinguishedName || it == O2SDKManager.instance().cId }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater?.inflate(layoutResId(), container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
        initView()
    }

    override fun onResume() {
        super.onResume()
        isSelfShow = true
    }

    override fun onPause() {
        super.onPause()
        isSelfShow = false
    }

}