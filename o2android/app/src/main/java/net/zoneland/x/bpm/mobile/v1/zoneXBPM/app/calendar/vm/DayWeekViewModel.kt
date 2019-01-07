package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.calendar.vm

import android.annotation.SuppressLint
import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.graphics.Color
import net.zoneland.o2.view.CalendarViewEvent
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2App
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseO2ViewModel
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.calendar.CalendarEventFilterInfo
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.CalendarEventFilterVO
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.DateHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import kotlin.collections.ArrayList

/**
 * Created by fancyLou on 21/06/2018.
 * Copyright © 2018 O2. All rights reserved.
 */


class DayWeekViewModel(app: Application) : BaseO2ViewModel(app)  {


    private val eventFilter: MutableLiveData<CalendarEventFilterVO> by lazy { MutableLiveData<CalendarEventFilterVO>() }
    private val eventList: MediatorLiveData<List<CalendarViewEvent>> = MediatorLiveData()

    init {
        eventList.addSource(eventFilter, { filter ->
            if (filter != null) {
                val startTime = filter.start
                val endTime = filter.end
                val calendarIds = filter.calendarIds
                if (startTime != null && endTime != null && calendarIds != null) {
                    val startStr = DateHelper.getDate(startTime.time) + " 00:00:00"
                    val endStr = DateHelper.getDate(endTime.time) + " 23:59:59"
                    loadDataFromNet(startStr, endStr, calendarIds, eventList)
                } else {
                    XLog.error("过滤条件之一为空！！！！")
                }
            } else {
                XLog.error("过滤条件为空！！！！")
            }
        })


    }

    /**
     * currentFilter
     * 当前的条件，用来给前台显示用的
     */
    fun currentFilter(): LiveData<CalendarEventFilterVO> = eventFilter

    /**
     * 日程事件列表
     */
    fun getEventList(): LiveData<List<CalendarViewEvent>> = eventList

    /**
     * 过滤日程事件的开始和结束时间
     */
    fun filter(filter: CalendarEventFilterVO) {
        eventFilter.value = filter
    }


    private fun loadDataFromNet(start: String, end: String, calendarIds: List<String>, data: MutableLiveData<List<CalendarViewEvent>>) {
        val filter = CalendarEventFilterInfo(calendarIds, O2SDKManager.instance().distinguishedName, start, end)
        XLog.info("filter:$filter")
        getCalendarAssembleService()?.let { service ->
            service.filterCalendarEventList(filter)
                    .subscribeOn(Schedulers.io())
                    .flatMap { res ->
                        val transFor = ArrayList<CalendarViewEvent>()
                        if (res.data != null) {
                            val wholeDayList = res.data.wholeDayEvents
                            wholeDayList.forEach { event ->
                                @SuppressLint("Range")
                                val color = try {
                                    Color.parseColor(event.color)
                                } catch (e: Exception) {
                                    XLog.error("transform color error ", e)
                                    Color.RED
                                }
                                val cEvent = CalendarViewEvent(
                                        DateHelper.gc(event.startTimeStr, "yyyy-MM-dd HH:mm:ss"),
                                        DateHelper.gc(event.endTimeStr, "yyyy-MM-dd HH:mm:ss"),
                                        event.title,
                                        color,
                                        true,
                                        event
                                )
                                transFor.add(cEvent)
                            }
                            res.data.inOneDayEvents.forEach { oneDay ->
                                oneDay.inOneDayEvents.forEach { event ->
                                    @SuppressLint("Range")
                                    val color = try {
                                        Color.parseColor(event.color)
                                    } catch (e: Exception) {
                                        XLog.error("transform color error ", e)
                                        Color.RED
                                    }
                                    val cEvent = CalendarViewEvent(
                                            DateHelper.gc(event.startTimeStr, "yyyy-MM-dd HH:mm:ss"),
                                            DateHelper.gc(event.endTimeStr, "yyyy-MM-dd HH:mm:ss"),
                                            event.title,
                                            color,
                                            false,
                                            event
                                    )
                                    transFor.add(cEvent)
                                }
                            }
                        }

                        Observable.just(transFor)
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .o2Subscribe {
                        onNext { res ->
                            data.value = res
                        }
                        onError { e, isNetworkError ->
                            XLog.error("查询月视图数据出错 network:$isNetworkError", e)
                            data.value = ArrayList()
                        }
                    }
        }

    }
}