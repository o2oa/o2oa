package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.calendar

import android.databinding.BindingAdapter
import android.text.TextUtils
import android.view.View
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.DateHelper
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by fancyLou on 02/07/2018.
 * Copyright © 2018 O2. All rights reserved.
 */


object CalendarOB {

    const val ALL_DAY_DATE_FORMAT = "yyyy年MM月dd日"
    const val NOT_ALL_DAY_DATE_FORMAT = "yyyy年MM月dd日 HH:mm"
    const val FREQ = "FREQ"
    const val UNTIL = "UNTIL"
    const val BYDAY = "BYDAY" //重复周几
    const val WEEKLY = "WEEKLY"
    const val DAILY = "DAILY"
    const val MONTHLY = "MONTHLY"
    const val YEARLY = "YEARLY"
    const val NONE = "NONE"

    val deepColor = linkedMapOf(
            0 to "#428ffc",
            1 to "#5bcc61",
            2 to "#f9bf24",
            3 to "#f75f59",
            4 to "#f180f7",
            5 to "#9072f1",
            6 to "#909090",
            7 to "#1462be"
    )
    val lightColor = linkedMapOf(
            0 to "#cae2ff",
            1 to "#d0f1b0",
            2 to "#fef4bb",
            3 to "#fdd9d9",
            4 to "#f4c5f7",
            5 to "#d6ccf9",
            6 to "#e7e7e7",
            7 to "#cae2ff"
    )

    val calendarTypes = linkedMapOf(
            "PERSON" to "个人日历",
            "UNIT" to "组织日历"
    )

    val remindOptions = linkedMapOf(
            NONE to "不提醒",
            "-5_s" to "开始时",
            "-5_m" to "提前5分钟",
            "-10_m" to "提前10分钟",
            "-15_m" to "提前15分钟",
            "-30_m" to "提前30分钟",
            "-1_h" to "提前1小时",
            "-2_h" to "提前2小时"
    )

    val repeatOptions = linkedMapOf(
            NONE to "不重复",
            DAILY to "每天",
            WEEKLY to "每周",
            MONTHLY to "每月（当日）",
            YEARLY to "每年（当日）"
    )

    //SU,MO,TU,WE,TH,FR,SA
    val weekDays = linkedMapOf(
            "SU" to "周日",
            "MO" to "周一",
            "TU" to "周二",
            "WE" to "周三",
            "TH" to "周四",
            "FR" to "周五",
            "SA" to "周六"
    )


    /**
     * 时间转化成 weekday
     */
    fun weekKey(date: String?, format: String): String {
        try {
            val c = DateHelper.gc(date, format)
            if (c != null) {
                val weekDay = c.get(Calendar.DAY_OF_WEEK)
                when (weekDay) {
                    Calendar.SUNDAY -> return "SU"
                    Calendar.MONDAY -> return "MO"
                    Calendar.TUESDAY -> return "TU"
                    Calendar.WEDNESDAY -> return "WE"
                    Calendar.THURSDAY -> return "TH"
                    Calendar.FRIDAY -> return "FR"
                    Calendar.SATURDAY -> return "SA"
                }
            }
        } catch (e: Exception) {
        }
        return "SU"
    }


    /**
     *
     */
    fun dateFormat2Server(date: String?, isAllDay: Boolean):String {
        val day = if (isAllDay) {
            DateHelper.convertStringToDate(ALL_DAY_DATE_FORMAT, date)
        }else {
            DateHelper.convertStringToDate(NOT_ALL_DAY_DATE_FORMAT, date)
        }
        if (day!=null) {
            return if (isAllDay) {
                DateHelper.getDateTime("yyyy-MM-dd", day) + " 00:00:00"
            }else {
                DateHelper.getDateTime("yyyy-MM-dd HH:mm", day) + ":00"
            }
        }
        return ""
    }

    /**
     * 后台数据转化成前台展现的格式
     */
    fun dateFormate2Frontend(date: String?, isAllDay: Boolean): String {
        val day = DateHelper.convertStringToDate("yyyy-MM-dd HH:mm:ss", date)
        if (day!=null) {
            return if (isAllDay) {
                DateHelper.getDateTime(ALL_DAY_DATE_FORMAT, day)
            }else {
                DateHelper.getDateTime(NOT_ALL_DAY_DATE_FORMAT, day)
            }
        }
        return ""
    }

    /**
     * 重复规则编码
     */
    fun rruleEncode(freq: String?, repeatWeekList: List<String>? = ArrayList(), untilDate: String? = NONE): String {
        if (TextUtils.isEmpty(freq) || freq == NONE) {
            return ""
        }
        var mFreq = "$FREQ=$freq"
        val until = formatUntilDate(untilDate)
        if (!TextUtils.isEmpty(until)) {
            mFreq = "$mFreq;$UNTIL=$until"
        }
        if (freq == WEEKLY && repeatWeekList!=null && repeatWeekList.isNotEmpty()) {
            mFreq = mFreq + ";" +BYDAY + "=" + repeatWeekList.joinToString(",")
        }
        return mFreq
    }


    /**
     * 提醒转化函数
     */
    fun remindDecode(remind:String?): String{
        val array = arrayOf("0", "0", "0", "0")
        if (!TextUtils.isEmpty(remind) && remind!= NONE) {
            val split = remind?.split("_")
            if (split!=null) {
                when(split[1]){
                    "s" -> {
                        array[3] = split[0]
                    }
                    "m" -> {
                        array[2] = split[0]
                    }
                    "h" -> {
                        array[1] = split[0]
                    }
                }
            }
        }
        return array.joinToString(",")
    }



    private fun formatUntilDate(date: String?): String {
        if (TextUtils.isEmpty(date) || date == NONE) {
            return ""
        }
        val day = DateHelper.convertStringToDate(ALL_DAY_DATE_FORMAT, date)
        if (day != null) {
            return DateHelper.getDateTime("yyyyMMdd", day) + "T000000Z"
        }
        return ""
    }


    @BindingAdapter("visibleOrGone")
    @JvmStatic
    fun visibleOrGone(view: View, visible: Boolean) {
        if (visible) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    }
}