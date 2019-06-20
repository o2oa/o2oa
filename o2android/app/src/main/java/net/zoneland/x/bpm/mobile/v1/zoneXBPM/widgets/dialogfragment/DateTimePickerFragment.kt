package net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.dialogfragment

import android.os.Build
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.DatePicker
import android.widget.TimePicker
import kotlinx.android.synthetic.main.dialog_fragment_date_time_picker.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.DateHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.gone
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible
import java.util.*


/**
 * Created by fancyLou on 2019-05-06.
 * Copyright © 2019 O2. All rights reserved.
 */


class DateTimePickerFragment: DialogFragment() {

    interface OnDateTimeSetListener {
        fun onSet(time: String, pickerType: String)
    }

    companion object {
        const val PICKER_TYPE = "picker_type"
        const val DEFAULT_TIME = "default_time"
        const val DATEPICKER_TYPE = "date"
        const val TIMEPICKER_TYPE = "time"
        const val DATETIMEPICKER_TYPE = "dateTime"
    }

    lateinit var pickerType: String // date time dateTime
    lateinit var defaultTime: Calendar // 默认选中时间
    private var listener: DateTimePickerFragment.OnDateTimeSetListener? = null

    fun setListener(listener: DateTimePickerFragment.OnDateTimeSetListener) {
        this.listener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
        pickerType = arguments.getString(PICKER_TYPE) ?: DATEPICKER_TYPE
        val dTime = arguments.getString(DEFAULT_TIME)
        XLog.debug("pickerType:$pickerType, defaultTime: $dTime")
        val date = if (TextUtils.isEmpty(dTime)){
            Date()
        }else {
             when(pickerType) {
                 DATEPICKER_TYPE -> DateHelper.convertStringToDate("yyyy-MM-dd", dTime)
                 TIMEPICKER_TYPE -> DateHelper.convertStringToDate("HH:mm", dTime)
                else -> DateHelper.convertStringToDate("yyyy-MM-dd HH:mm", dTime)
            }
        }
        defaultTime = Calendar.getInstance()
        defaultTime.time = date ?: Date()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (inflater == null) {
            return super.onCreateView(inflater, container, savedInstanceState)
        }
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setDimAmount(0.8f)
        return inflater.inflate(net.zoneland.x.bpm.mobile.v1.zoneXBPM.R.layout.dialog_fragment_date_time_picker, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        datePickerView.descendantFocusability = DatePicker.FOCUS_BLOCK_DESCENDANTS
        timePickerView.descendantFocusability = TimePicker.FOCUS_BLOCK_DESCENDANTS
        timePickerView.setIs24HourView(true)
        when(pickerType) {
            DATEPICKER_TYPE -> {
                datePickerView.updateDate(defaultTime.get(Calendar.YEAR), defaultTime.get(Calendar.MONTH), defaultTime.get(Calendar.DAY_OF_MONTH))
                datePickerView.visible()
                date_time_pick_split1.visible()
                timePickerView.gone()
                date_time_pick_split2.gone()
            }
            TIMEPICKER_TYPE -> {
                datePickerView.gone()
                date_time_pick_split1.gone()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    timePickerView.hour = defaultTime.get(Calendar.HOUR_OF_DAY)
                    timePickerView.minute = defaultTime.get(Calendar.MINUTE)
                }else {
                    timePickerView.currentHour = defaultTime.get(Calendar.HOUR_OF_DAY)
                    timePickerView.currentMinute = defaultTime.get(Calendar.MINUTE)
                }
                timePickerView.visible()
                date_time_pick_split2.visible()
            }
            else -> {
                datePickerView.updateDate(defaultTime.get(Calendar.YEAR), defaultTime.get(Calendar.MONTH), defaultTime.get(Calendar.DAY_OF_MONTH))
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    timePickerView.hour = defaultTime.get(Calendar.HOUR_OF_DAY)
                    timePickerView.minute = defaultTime.get(Calendar.MINUTE)
                }else {
                    timePickerView.currentHour = defaultTime.get(Calendar.HOUR_OF_DAY)
                    timePickerView.currentMinute = defaultTime.get(Calendar.MINUTE)
                }
                datePickerView.visible()
                date_time_pick_split1.visible()
                timePickerView.visible()
                date_time_pick_split2.visible()
            }
        }
        back.setOnClickListener {
            closeSelf()
        }
        ensure.setOnClickListener {
            val selected = getSelectedTime()
            listener?.onSet(selected, pickerType)
            closeSelf()
        }
    }
    private fun getSelectedTime(): String {
        val selectedDate = Calendar.getInstance()
        return when(pickerType) {
            DATEPICKER_TYPE -> {
                selectedDate.set(datePickerView.year, datePickerView.month, datePickerView.dayOfMonth, 0, 0, 0)
                DateHelper.getDate(selectedDate.time)
            }
            TIMEPICKER_TYPE -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    selectedDate.set(selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.DAY_OF_MONTH), timePickerView.hour, timePickerView.minute, 0)
                } else {
                    selectedDate.set(selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.DAY_OF_MONTH), timePickerView.currentHour, timePickerView.currentMinute, 0)
                }
                DateHelper.getDateTime("HH:mm", selectedDate.time)
            }
            else -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    selectedDate.set(datePickerView.year, datePickerView.month, datePickerView.dayOfMonth, timePickerView.hour, timePickerView.minute, 0)
                } else {
                    selectedDate.set(datePickerView.year, datePickerView.month, datePickerView.dayOfMonth, timePickerView.currentHour, timePickerView.currentMinute, 0)
                }
                DateHelper.getDateTime("yyyy-MM-dd HH:mm", selectedDate.time)
            }
        }
    }

    private fun closeSelf() {
        dismissAllowingStateLoss()
    }
}