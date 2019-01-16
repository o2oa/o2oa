package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.calendar.vm

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2App
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseO2ViewModel
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.calendar.CalendarEventFilterInfo
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.calendar.CalendarEventInfoData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.CalendarEventFilterVO
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.DateHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by fancyLou on 19/06/2018.
 * Copyright © 2018 O2. All rights reserved.
 */


class MonthCalendarViewModel(app: Application) : BaseO2ViewModel(app) {

    private val eventFilter: MutableLiveData<CalendarEventFilterVO> by lazy { MutableLiveData<CalendarEventFilterVO>() }
    private val oneDayEventMap: MediatorLiveData<Map<String, ArrayList<CalendarEventInfoData>>> = MediatorLiveData()
    private val selectDay: MutableLiveData<Calendar> by lazy { MutableLiveData<Calendar>() }
    private val dayEventList: MediatorLiveData<List<CalendarEventInfoData>> = MediatorLiveData()


    init {
        oneDayEventMap.addSource(eventFilter, { filter ->
            val start = filter?.start
            val end = filter?.end
            val calendarIds = filter?.calendarIds
            if (start != null && end!= null && calendarIds!=null) {
                loadDataFromNet(start, end, calendarIds, oneDayEventMap)
            } else {
                XLog.error("selectMonth is null")
            }
        })

        dayEventList.addSource(selectDay, { day ->
            if (day != null) {
                val dayStr = DateHelper.getDate(day.time)
                if (oneDayEventMap.value != null) {
                    dayEventList.postValue(oneDayEventMap.value!![dayStr] ?: ArrayList())
                }
            }else {
                XLog.error("selectDay is null")
            }
        })
        dayEventList.addSource(oneDayEventMap, { map ->
            val day = selectDay.value
            if (map != null && day != null) {
                val dayStr = DateHelper.getDate(day.time)
                dayEventList.postValue(map[dayStr] ?: ArrayList())
            }else {
                XLog.error("map or selectDay is null")
            }
        })
    }


    /**
     * 过滤日程事件的开始和结束时间
     */
    fun filter(filter: CalendarEventFilterVO) {
        eventFilter.value = filter
    }

    /**
     * 选中日期
     */
    fun selectDay(day: Calendar) {
        selectDay.value = day
    }

    /**
     * currentFilter
     * 当前的条件，用来给前台显示用的
     */
    fun currentFilter(): LiveData<CalendarEventFilterVO> = eventFilter

    fun getEventMap(): LiveData<Map<String, ArrayList<CalendarEventInfoData>>> = oneDayEventMap

    fun getSelectDay(): LiveData<Calendar> = selectDay

    fun getDayEvenList(): LiveData<List<CalendarEventInfoData>> = dayEventList


    private fun loadDataFromNet(startTime: Calendar, endTime: Calendar, calendarIds: List<String>, data: MutableLiveData<Map<String, ArrayList<CalendarEventInfoData>>>) {
        val start = DateHelper.getDate(startTime.time)
        val end = DateHelper.getDate(endTime.time)
        val result = HashMap<String, ArrayList<CalendarEventInfoData>>()
        val filter = CalendarEventFilterInfo(calendarIds, O2SDKManager.instance().distinguishedName, "$start 00:00:00", "$end 23:59:59")
        XLog.info("filter:$filter")
        getCalendarAssembleService()?.let { service ->
            service.filterCalendarEventList(filter)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .o2Subscribe {
                        onNext { res ->
                            if (res.data != null) {
                                val wholeDayList = res.data.wholeDayEvents
                                wholeDayList.forEach { event ->
                                    val dayList = DateHelper.splitEveryDay(event.startTimeStr, event.endTimeStr)
                                    dayList.forEach { day ->
                                        val dayStr = DateHelper.getDate(day.time)
                                        if (result.containsKey(dayStr)) {
                                            result[dayStr]?.add(event)
                                        } else {
                                            result[dayStr] = arrayListOf(event)
                                        }
                                    }
                                }
                                res.data.inOneDayEvents.forEach { oneDay ->
                                    if (result.containsKey(oneDay.eventDate)) {
                                        result[oneDay.eventDate]?.addAll(oneDay.inOneDayEvents)
                                    } else {
                                        result[oneDay.eventDate] = oneDay.inOneDayEvents
                                    }
                                }
                            }
                            data.value = (result)
                        }
                        onError { e, isNetworkError ->
                            XLog.error("查询月视图数据出错 network:$isNetworkError", e)
                            data.value = (result)
                        }
                    }
        }

    }
}