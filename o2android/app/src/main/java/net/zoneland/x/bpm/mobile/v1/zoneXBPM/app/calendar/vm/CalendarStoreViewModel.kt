package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.calendar.vm

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseO2ViewModel
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.calendar.CalendarPostData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by fancyLou on 2018/7/17.
 * Copyright © 2018 O2. All rights reserved.
 */

class CalendarStoreViewModel(app: Application): BaseO2ViewModel(app) {


    private val publicList: MutableLiveData<List<CalendarPostData>> by lazy { MutableLiveData<List<CalendarPostData>>() }

    fun publicCalendarList(): LiveData<List<CalendarPostData>> = publicList


    fun loadPublicList() {
        val service = getCalendarAssembleService()
        service?.getPublicCalendarList()?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.o2Subscribe {
            onNext {
                publicList.value = it.data
            }
            onError { e, isNetworkError ->
                XLog.error("查询日历广场异常, isnet:$isNetworkError", e)
                publicList.value = null
            }
        }
    }

    fun followCalendar(id: String, isFollow: Boolean) {
        if (isFollow) {
            getCalendarAssembleService()?.followCalendarCancel(id)
        }else {
            getCalendarAssembleService()?.followCalendar(id)
        }?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.o2Subscribe {
                    onNext {
                        XLog.info("${it.data.isValue}")
                    }
                    onError { e, isNetworkError ->
                        XLog.error("isnet:$isNetworkError", e)
                    }
                }
    }
}