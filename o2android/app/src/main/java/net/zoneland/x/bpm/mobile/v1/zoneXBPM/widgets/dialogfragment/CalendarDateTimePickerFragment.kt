package net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.dialogfragment

import android.os.Build
import android.os.Bundle
import android.support.constraint.ConstraintSet
import android.support.transition.TransitionManager
import android.support.v4.app.DialogFragment
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TimePicker
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener
import kotlinx.android.synthetic.main.dialog_fragment_calendar_date_time_picker.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.DateHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.gone
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible
import java.util.*

/**
 * Created by fancyLou on 2019-05-15.
 * Copyright © 2019 O2. All rights reserved.
 */


class CalendarDateTimePickerFragment : DialogFragment() , OnDateSelectedListener, OnMonthChangedListener {

    interface OnDateTimeSetListener {
        fun onSet(time: String)
        fun onSetInterval(startDate: String, endDate: String)
    }

    companion object {
        //传入参数的key
        const val PICKER_TYPE_KEY = "picker_type"
        const val DEFAULT_VALUE_KEY = "default_value"
        const val DEFAULT_START_VALUE_KEY = "default_start_value"
        const val DEFAULT_END_VALUE_KEY = "default_end_value"
        // 类型
        const val DATE_PICKER_TYPE = "datePicker"
        const val DATE_TIME_PICKER_TYPE = "dateTimePicker"
        const val DATEINTERVAL_PICKER_TYPE = "dateIntervalPicker"
    }

    lateinit var pickerType: String // date time dateTime
    lateinit var defaultValue: String
    lateinit var defaultStartDate: String
    lateinit var defaultEndDate: String
    private var isStartDateCalendarMode = true //日历是显示开始时间的
    private val datePickerLayout: ConstraintSet by lazy { ConstraintSet() }
    private val dateTimePickerLayout: ConstraintSet by lazy { ConstraintSet() }
    private var setListener: OnDateTimeSetListener? = null

    fun setOnDateTimeSetListener(listener: OnDateTimeSetListener) {
        this.setListener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
        pickerType = arguments.getString(PICKER_TYPE_KEY) ?: DATE_PICKER_TYPE
        defaultValue = arguments.getString(DEFAULT_VALUE_KEY) ?: ""
        defaultStartDate = arguments.getString(DEFAULT_START_VALUE_KEY) ?: ""
        defaultEndDate = arguments.getString(DEFAULT_END_VALUE_KEY) ?: ""
    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (inflater == null) {
            return null
        }
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setDimAmount(0.8f)
        return inflater.inflate(R.layout.dialog_fragment_calendar_date_time_picker, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //布局
        datePickerLayout.clone(constraint_calendar_date_time_picker)
        dateTimePickerLayout.clone(activity, R.layout.dialog_fragment_calendar_date_time_picker2)

        //view init
        calendarView_date_picker.topbarVisible = false
        time_picker_calendar_picker.setIs24HourView(true)
        time_picker_calendar_picker.descendantFocusability = TimePicker.FOCUS_BLOCK_DESCENDANTS //禁止输入
        if (pickerType == DATE_TIME_PICKER_TYPE) {
            tv_time_value.visible()
        }else {
            tv_time_value.gone()
        }
        if (pickerType == DATEINTERVAL_PICKER_TYPE) {
            tv_end_date.visible()
        }else {
            tv_end_date.gone()
        }

        //设值
        when(pickerType) {
            DATE_PICKER_TYPE -> {
                var currentDate = Date()
                if (!TextUtils.isEmpty(defaultValue)) {
                    currentDate = DateHelper.convertStringToDate("yyyy-MM-dd", defaultValue)
                }
                calendarView_date_picker.setSelectedDate(currentDate)
                calendarView_date_picker.setCurrentDate(currentDate)
                setCalendarBg(currentDate)
                tv_start_date.text = DateHelper.getDate(currentDate)
            }
            DATE_TIME_PICKER_TYPE -> {
                var currentDate = Date()
                if (!TextUtils.isEmpty(defaultValue)) {
                    currentDate = DateHelper.convertStringToDate("yyyy-MM-dd HH:mm", defaultValue)
                }
                calendarView_date_picker.setSelectedDate(currentDate)
                calendarView_date_picker.setCurrentDate(currentDate)
                setCalendarBg(currentDate)
                tv_start_date.text = DateHelper.getDate(currentDate)
                tv_time_value.text = DateHelper.getDateTime("HH:mm", currentDate)
                //
                val cal = Calendar.getInstance()
                cal.time = currentDate
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    time_picker_calendar_picker.hour = cal.get(Calendar.HOUR_OF_DAY)
                    time_picker_calendar_picker.minute = cal.get(Calendar.MINUTE)
                }else {
                    time_picker_calendar_picker.currentHour = cal.get(Calendar.HOUR_OF_DAY)
                    time_picker_calendar_picker.currentMinute = cal.get(Calendar.MINUTE)
                }
            }
            DATEINTERVAL_PICKER_TYPE -> {
                var startDate = Date()
                var endDate = Date()
                if (!TextUtils.isEmpty(defaultStartDate)) {
                    startDate = DateHelper.convertStringToDate("yyyy-MM-dd", defaultStartDate)
                }
                if (!TextUtils.isEmpty(defaultEndDate)) {
                    endDate = DateHelper.convertStringToDate("yyyy-MM-dd", defaultEndDate)
                }
                calendarView_date_picker.setSelectedDate(startDate)
                calendarView_date_picker.setCurrentDate(startDate)
                setCalendarBg(startDate)
                tv_start_date.text = DateHelper.getDate(startDate)
                tv_end_date.text = DateHelper.getDate(endDate)
            }
        }

        //监听 事件等
        calendarView_date_picker.selectionMode
        calendarView_date_picker.setOnDateChangedListener(this)
        calendarView_date_picker.setOnMonthChangedListener(this)
        time_picker_calendar_picker.setOnTimeChangedListener { _, hourOfDay, minute ->
            val hour: String = if (hourOfDay > 9){ hourOfDay.toString()  }else{
                "0$hourOfDay"
            }
            val m: String = if(minute >9) { minute.toString() } else { "0$minute" }
            tv_time_value.text = "$hour:$m"
        }
        tv_start_date.setOnClickListener {
            if (pickerType == DATE_TIME_PICKER_TYPE) {
                animationForCalendarVisible()
            }else if (pickerType == DATEINTERVAL_PICKER_TYPE) {
                showStartDateCalendar()
            }
        }
        tv_time_value.setOnClickListener {
            animationForTimePickerVisible()
        }
        tv_end_date.setOnClickListener {
            showEndDateCalendar()
        }
        back.setOnClickListener {
            closeSelf()
        }
        ensure.setOnClickListener {
            //返回值
            setResult()
            closeSelf()
        }
    }

    override fun onDateSelected(p0: MaterialCalendarView, p1: CalendarDay, p2: Boolean) {
        if (isStartDateCalendarMode) {
            tv_start_date.text = DateHelper.getDate(p1.date)
            //如果是DATE_TIME_PICKER_TYPE类型 切换到时间选择器
            if (pickerType == DATE_TIME_PICKER_TYPE) {
                animationForTimePickerVisible()
            } else if (pickerType == DATEINTERVAL_PICKER_TYPE) {
                showEndDateCalendar()
            }
        }else {
            tv_end_date.text = DateHelper.getDate(p1.date)
        }
    }

    override fun onMonthChanged(p0: MaterialCalendarView?, p1: CalendarDay?) {
        if (p1!= null) {
            setCalendarBg(p1.date)
        }
    }



    private fun setResult() {
        when(pickerType) {
            DATE_PICKER_TYPE -> {
                setListener?.onSet(tv_start_date.text.toString())
            }
            DATE_TIME_PICKER_TYPE -> {
                setListener?.onSet(tv_start_date.text.toString() + " " + tv_time_value.text.toString())
            }
            else -> {
                setListener?.onSetInterval(tv_start_date.text.toString(), tv_end_date.text.toString())
            }
        }
    }


    private fun showStartDateCalendar() {
        isStartDateCalendarMode = true
        tv_start_date.setTextColor(ContextCompat.getColor(activity, R.color.icon_blue))
        tv_end_date.setTextColor(ContextCompat.getColor(activity, R.color.z_color_text_primary))
        val sDate = DateHelper.convertStringToDate("yyyy-MM-dd", tv_start_date.text.toString())
        calendarView_date_picker.setSelectedDate(sDate)
        calendarView_date_picker.setCurrentDate(sDate)
    }

    private fun showEndDateCalendar() {
        isStartDateCalendarMode = false
        tv_start_date.setTextColor(ContextCompat.getColor(activity, R.color.z_color_text_primary))
        tv_end_date.setTextColor(ContextCompat.getColor(activity, R.color.icon_blue))
        val eDate = DateHelper.convertStringToDate("yyyy-MM-dd", tv_end_date.text.toString())
        calendarView_date_picker.setSelectedDate(eDate)
        calendarView_date_picker.setCurrentDate(eDate)

    }
    /**
     * 动画显示日历选择器
     */
    private fun animationForCalendarVisible() {
        TransitionManager.beginDelayedTransition(constraint_calendar_date_time_picker)
        datePickerLayout.applyTo(constraint_calendar_date_time_picker)
        // tv_first_value 高亮
        tv_start_date.setTextColor(ContextCompat.getColor(activity, R.color.icon_blue))
        tv_time_value.setTextColor(ContextCompat.getColor(activity, R.color.z_color_text_primary))
    }

    /**
     * 动画显示时间选择器
     */
    private fun animationForTimePickerVisible() {
        TransitionManager.beginDelayedTransition(constraint_calendar_date_time_picker)
        dateTimePickerLayout.applyTo(constraint_calendar_date_time_picker)
        // tv_second_value 高亮
        tv_start_date.setTextColor(ContextCompat.getColor(activity, R.color.z_color_text_primary))
        tv_time_value.setTextColor(ContextCompat.getColor(activity, R.color.icon_blue))
    }

    /**
     * 设值日历背景 月份
     */
    private fun setCalendarBg(date: Date) {
        val thisYear = DateHelper.nowByFormate("yyyy年")
        val dateYear = DateHelper.getDateTime("yyyy年", date)
        val dateMonth = DateHelper.getDateTime("MM月", date)
        if (thisYear != dateYear) {
            tv_calendar_picker_bg.text = dateYear+dateMonth
        }else {
            tv_calendar_picker_bg.text = dateMonth
        }
    }

    private fun closeSelf() {
        dismissAllowingStateLoss()
    }
}