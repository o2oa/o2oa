package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.calendar.vm

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.text.Html
import android.text.TextUtils
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseO2ViewModel
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.calendar.CalendarOB
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.calendar.CalendarEventInfoData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.calendar.CalendarInfoData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.calendar.CalendarInfoPickViewData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.CalendarPickerOption
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.FrontendResponse
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.DateHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.HtmlRegexpUtil
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*

/**
 * Created by fancyLou on 02/07/2018.
 * Copyright © 2018 O2. All rights reserved.
 */


class CreateEventViewModel(app: Application) : BaseO2ViewModel(app) {

    /**
     * 修改的时候才有下面两个字段值
     */
    val eventId: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val recurrenceRule: MutableLiveData<String> by lazy { MutableLiveData<String>() }

    //form
    val eventTitle: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val calendarId: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val calendarName: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val calendarColor: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val remindValue: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val remindName: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val comment: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val repeatValue: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val repeatName: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val untilDate: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val isLoading: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    //网络操作反馈结果
    val netResponse: MutableLiveData<FrontendResponse> by lazy { MutableLiveData<FrontendResponse>() }

//    val locationName: MutableLiveData<String> by lazy { MutableLiveData<String>() }
//    val longitude: MutableLiveData<String> by lazy { MutableLiveData<String>() }
//    val latitude: MutableLiveData<String> by lazy { MutableLiveData<String>() }


    val startTime = MediatorLiveData<String>()
    val endTime = MediatorLiveData<String>()
    val isAllDayEvent = MutableLiveData<Boolean>()
    val eventColor = MediatorLiveData<String>()
    val repeatWeekList = MediatorLiveData<List<String>>()
    val repeatWeekDaysVisible = MediatorLiveData<Boolean>()
    val repeatUntilDateVisibleable = MediatorLiveData<Boolean>()
    val untilDateClearBtnVisibleable = MediatorLiveData<Boolean>()
    val deleteEventBtnVisibleable = MediatorLiveData<Boolean>()



    init {
        isLoading.value = false
        isAllDayEvent.value = false
        repeatWeekDaysVisible.value = false
        untilDate.value = CalendarOB.NONE
        startTime.addSource(isAllDayEvent, {
            initTime()
        })
        endTime.addSource(isAllDayEvent, {
            initTime()
        })
        eventColor.addSource(calendarColor, { color ->
            if (color != null) {
                eventColor.value = color
            }
        })
        repeatWeekList.addSource(repeatValue, { v ->
            if (v != null && v == CalendarOB.WEEKLY) { //每周重复
                try {
                    val weekday = if (isAllDayEvent.value == true) {
                        CalendarOB.weekKey(startTime.value, CalendarOB.ALL_DAY_DATE_FORMAT)
                    } else {
                        CalendarOB.weekKey(startTime.value, CalendarOB.NOT_ALL_DAY_DATE_FORMAT)
                    }
                    repeatWeekList.value = arrayListOf(weekday)
                } catch (e: Exception) {
                }
            }
        })
        repeatWeekDaysVisible.addSource(repeatValue, { v ->
            repeatWeekDaysVisible.value = v != null && v == CalendarOB.WEEKLY
        })
        repeatUntilDateVisibleable.addSource(repeatValue, { v ->
            repeatUntilDateVisibleable.value = v != null && v != CalendarOB.NONE
        })
        untilDateClearBtnVisibleable.addSource(untilDate, { until ->
            untilDateClearBtnVisibleable.value = until != null && until != CalendarOB.NONE
        })
        deleteEventBtnVisibleable.addSource(eventId, { id->
            deleteEventBtnVisibleable.value =  !TextUtils.isEmpty(id)
        })
        initTime()
    }

    /**
     * observer eventColor
     */
    fun eventColorLive(): LiveData<String> = eventColor

    /**
     * observer repeat week list
     */
    fun repeatWeekListLive(): LiveData<List<String>> = repeatWeekList

    /**
     * observer eventId 更新的时候回填数据 restoreEventInfo
     */
    fun eventIdLive(): LiveData<String> = eventId


    /**
     * 判断开始时间和结束时间是否正确
     */
    fun isTimeCorrect(): Boolean {
        val start = if (isAllDayEvent.value == true) DateHelper.gc(startTime.value, CalendarOB.ALL_DAY_DATE_FORMAT) else DateHelper.gc(startTime.value, CalendarOB.NOT_ALL_DAY_DATE_FORMAT)
        val end = if (isAllDayEvent.value == true) DateHelper.gc(endTime.value, CalendarOB.ALL_DAY_DATE_FORMAT) else DateHelper.gc(endTime.value, CalendarOB.NOT_ALL_DAY_DATE_FORMAT)
        if (start == null || end == null) {
            return false
        }
        return (start.timeInMillis < end.timeInMillis)
    }

    /**
     * 新增日程事件
     */
    fun saveEvent() {
        isLoading.value = true
        val info = CalendarEventInfoData()
        info.title = eventTitle.value ?: ""
        info.isAllDayEvent = isAllDayEvent.value ?: false
        info.startTime = CalendarOB.dateFormat2Server(startTime.value, info.isAllDayEvent)
        info.endTime = CalendarOB.dateFormat2Server(endTime.value, info.isAllDayEvent)
        info.calendarId = calendarId.value ?: ""
        info.color = eventColor.value ?: ""
        info.recurrenceRule = CalendarOB.rruleEncode(repeatValue.value, repeatWeekList.value, untilDate.value)
        info.valarmTime_config = CalendarOB.remindDecode(remindValue.value)
        info.comment = comment.value ?: ""

        XLog.info("event: $info")
        val service = getCalendarAssembleService()
        if (service == null) {
            isLoading.value = false
            val res = FrontendResponse(false, "日程服务模块异常！")
            netResponse.value = res
        } else {
            service.saveCalendarEvent(info)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .o2Subscribe {
                        onNext {
                            isLoading.value = false
                            val res = FrontendResponse(true, "保存成功！")
                            netResponse.value = res

                        }
                        onError { e, isNetworkError ->
                            XLog.error("保存日程事件失败， isNetworkError:$isNetworkError", e)
                            isLoading.value = false
                            val res = FrontendResponse(false, "保存异常")
                            netResponse.value = res

                        }
                    }
        }
    }

    /**
     * 修改日程事件
     * @param type 单个：0, 之后：1, 全部：2
     */
    fun updateEvent(type: Int) {
        isLoading.value = true
        val info = CalendarEventInfoData()
        info.id = eventId.value ?: ""
        info.title = eventTitle.value ?: ""
        info.isAllDayEvent = isAllDayEvent.value ?: false
        info.startTime = CalendarOB.dateFormat2Server(startTime.value, info.isAllDayEvent)
        info.endTime = CalendarOB.dateFormat2Server(endTime.value, info.isAllDayEvent)
        info.calendarId = calendarId.value ?: ""
        info.color = eventColor.value ?: ""
        info.recurrenceRule = CalendarOB.rruleEncode(repeatValue.value, repeatWeekList.value, untilDate.value)
        info.valarmTime_config = CalendarOB.remindDecode(remindValue.value)
        info.comment = comment.value ?: ""
        XLog.info("type: $type, event: $info")
        val service = getCalendarAssembleService()
        if (service == null) {
            isLoading.value = false
            val res = FrontendResponse(false, "日程服务模块异常！")
            netResponse.value = res
        } else {
            val update = when (type) {
                0 -> {
                    service.updateCalendarEventSingle(info.id, info)
                }
                1 -> {
                    service.updateCalendarEventAfter(info.id, info)
                }
                else -> {
                    service.updateCalendarEventAll(info.id, info)
                }
            }
            update.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .o2Subscribe {
                        onNext { res ->
                            XLog.info("update success, ${res.data}")
                            isLoading.value = false
                            val fr = FrontendResponse(true, "更新成功！")
                            netResponse.value = fr
                        }
                        onError { e, isNetworkError ->
                            XLog.error("update fail, isnet:$isNetworkError", e)
                            isLoading.value = false
                            val res = FrontendResponse(false, "更新异常")
                            netResponse.value = res
                        }
                    }
        }
    }

    /**
     * 删除日程事件
     * @param type 单个：0, 之后：1, 全部：2
     */
    fun deleteEvent(type: Int) {
        isLoading.value = true
        val id = eventId.value
        val service = getCalendarAssembleService()
        if (service == null || id == null) {
            isLoading.value = false
            val res = FrontendResponse(false, "日程服务模块异常！")
            netResponse.value = res
        } else {
            XLog.info("delete type: $type ")
            val delete = when (type) {
                0 -> {
                    service.deleteCalendarEventSingle(id)
                }
                1 -> {
                    service.deleteCalendarEventAfter(id)
                }
                else -> {
                    service.deleteCalendarEventAll(id)
                }
            }
            delete.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .o2Subscribe {
                        onNext { res ->
                            XLog.info("delete success, ${res.data}")
                            isLoading.value = false
                            val fr = FrontendResponse(true, "删除成功！")
                            netResponse.value = fr
                        }
                        onError { e, isNetworkError ->
                            XLog.error("update fail, isnet:$isNetworkError", e)
                            isLoading.value = false
                            val res = FrontendResponse(false, "删除异常")
                            netResponse.value = res
                        }
                    }
        }
    }

    fun setStartTimeAndFormat(millseconds: Long) {
        val date = Date(millseconds)
        if (isAllDayEvent.value == true) {
            startTime.value = DateHelper.getDateTime(CalendarOB.ALL_DAY_DATE_FORMAT, date)
        } else {
            startTime.value = DateHelper.getDateTime(CalendarOB.NOT_ALL_DAY_DATE_FORMAT, date)
        }
    }

    fun setEndTimeAndFormat(millseconds: Long) {
        val date = Date(millseconds)
        if (isAllDayEvent.value == true) {
            endTime.value = DateHelper.getDateTime(CalendarOB.ALL_DAY_DATE_FORMAT, date)
        } else {
            endTime.value = DateHelper.getDateTime(CalendarOB.NOT_ALL_DAY_DATE_FORMAT, date)
        }
    }

    fun setUntilDateAndFormat(millseconds: Long) {
        if (millseconds < 0) {
            untilDate.value = CalendarOB.NONE
        } else {
            val date = Date(millseconds)
            untilDate.value = DateHelper.getDateTime(CalendarOB.ALL_DAY_DATE_FORMAT, date)
        }
    }

    fun setCalendar(calendar: CalendarInfoPickViewData) {
        calendarId.value = calendar.id
        calendarName.value = calendar.name
        calendarColor.value = calendar.color
    }

    fun setSelectedRemind(option: CalendarPickerOption) {
        remindValue.value = option.value
        remindName.value = option.name
    }

    fun setSelectedRepeat(option: CalendarPickerOption) {
        repeatValue.value = option.value
        repeatName.value = option.name
    }

    fun setEventUpdateObserver(id: String, eventRule: String) {
        eventId.value = id
        recurrenceRule.value = eventRule
    }
    /**
     * 修改日程的时候 原对象回填数据
     */
    fun restoreEventInfo(eventInfoData: CalendarEventInfoData, myCalendars: List<CalendarInfoPickViewData>) {
        eventTitle.value = eventInfoData.title
        //先设置isallday 再设置时间
        isAllDayEvent.value = eventInfoData.isAllDayEvent
        startTime.value = CalendarOB.dateFormate2Frontend(eventInfoData.startTimeStr, eventInfoData.isAllDayEvent)
        endTime.value = CalendarOB.dateFormate2Frontend(eventInfoData.endTimeStr, eventInfoData.isAllDayEvent)
        val cal = myCalendars.filter { it.id == eventInfoData.calendarId }
        if (cal.isNotEmpty()) {
            setCalendar(cal[0])
        }
        eventColor.value = eventInfoData.color
        //提醒规则
        val remindConfig = eventInfoData.valarmTime_config
        transBackRemindConfig(remindConfig)
        //重复规则
        rruleDecode(eventInfoData.recurrenceRule)
        comment.value = HtmlRegexpUtil.filterHtml(eventInfoData.comment)
    }

    /**
     * FREQ\u003dWEEKLY;BYDAY\u003dSU,MO,TH
     */
    private fun rruleDecode(rrule: String) {
        if (!TextUtils.isEmpty(rrule)) {
            val ruleArr = rrule.split(";")
            ruleArr.forEach {
                val stageArray = it.split("=")
                if (stageArray.isNotEmpty()) {
                    val key = stageArray[0]
                    when (key) {
                        CalendarOB.FREQ -> {
                            setRepeatOption(stageArray[1])
                        }
                        CalendarOB.BYDAY -> {
                            repeatWeekList.value = stageArray[1].split(",")
                        }
                        CalendarOB.UNTIL -> {
                            val time = stageArray[1]
                            val day = DateHelper.convertStringToDate("yyyyMMdd", time.substring(0, 8))
                            untilDate.value = DateHelper.getDateTime(CalendarOB.ALL_DAY_DATE_FORMAT, day)
                        }
                    }
                }

            }
        }
    }

    private fun setRepeatOption(value: String) {
        if (TextUtils.isEmpty(value)) {
            repeatName.value = CalendarOB.repeatOptions[CalendarOB.NONE]
            repeatValue.value = CalendarOB.NONE
        } else {
            repeatName.value = CalendarOB.repeatOptions[value]
            repeatValue.value = value
        }
    }


    private fun transBackRemindConfig(remindConfig: String) {
        if (!TextUtils.isEmpty(remindConfig)) {
            val arr = remindConfig.split(",")
            var reValue = ""
            run breakOut@{
                arr.forEachIndexed { index, time ->
                    if (time != "0") {
                        val re = when (index) {
                            0 -> {
                                "${time}_d"
                            }
                            1 -> {
                                "${time}_h"
                            }
                            2 -> {
                                "${time}_m"
                            }
                            3 -> {
                                "${time}_s"
                            }
                            else -> ""
                        }
                        if (!TextUtils.isEmpty(re)) {
                            reValue = re
                            return@breakOut
                        }
                    }
                }
            }
            if (TextUtils.isEmpty(reValue)) {
                reValue = CalendarOB.NONE
            }
            val reName = CalendarOB.remindOptions[reValue]
            if (reName != null) {
                remindName.value = reName
                remindValue.value = reValue
            }
        }
    }


    private fun initTime() =
            if (isAllDayEvent.value == true) {
                val today = DateHelper.nowByFormate(CalendarOB.ALL_DAY_DATE_FORMAT)
                val nextDay = DateHelper.getDateTime(CalendarOB.ALL_DAY_DATE_FORMAT, DateHelper.addDay(Date(), 1))
                startTime.value = today
                endTime.value = nextDay
            } else {
                val dayString = DateHelper.nowByFormate(CalendarOB.ALL_DAY_DATE_FORMAT)
                val cal = Calendar.getInstance()
                val hour = cal.get(Calendar.HOUR_OF_DAY) + 1
                val hourStr = if (hour > 9) hour.toString() else "0$hour"
                val nextHour = hour + 1
                val nextHourStr = if (nextHour > 9) nextHour.toString() else "0$nextHour"
                startTime.value = "$dayString $hourStr:00"
                endTime.value = "$dayString $nextHourStr:00"
            }

}