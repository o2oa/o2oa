package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.calendar.vm

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseO2ViewModel
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.group.Group
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.calendar.CalendarInfoData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.calendar.CalendarInfoPickViewData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by fancyLou on 27/06/2018.
 * Copyright © 2018 O2. All rights reserved.
 */

class CalendarViewModel(app: Application) : BaseO2ViewModel(app) {


    private val groups: MutableLiveData<List<Group<String, CalendarInfoPickViewData>>> by lazy {
        MutableLiveData<List<Group<String, CalendarInfoPickViewData>>>()
    }

    fun getGroups(): LiveData<List<Group<String, CalendarInfoPickViewData>>> = groups

    fun loadCalendarGroups() {
        getCalendarAssembleService()?.let { service ->
            service.myCalendarList()
                    .subscribeOn(Schedulers.io())
                    .flatMap { res ->
                        val list = ArrayList<Group<String, CalendarInfoPickViewData>>()
                        if (res.data != null) {
                            val group1 = Group("我的日历", res.data.myCalendars.map { CalendarInfoPickViewData(it.id, it.name, it.type, it.color, it.manageable) })
                            val group2 = Group("组织日历", res.data.unitCalendars.map { CalendarInfoPickViewData(it.id, it.name, it.type, it.color, it.manageable) })
                            val group3 = Group("关注的日历", res.data.followCalendars.map { CalendarInfoPickViewData(it.id, it.name, it.type, it.color, it.manageable) })
                            list.add(group1)
                            list.add(group2)
                            list.add(group3)
                        }
                        Observable.just(list)
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .o2Subscribe {
                        onNext { list ->
                            groups.value = list
                        }
                        onError { e, isNetworkError ->
                            XLog.error("我的日历查询异常", e)
                            groups.value = null
                        }
                    }
        }
    }


}