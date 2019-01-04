package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.calendar

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import kotlinx.android.synthetic.main.fragment_calendar_week.*
import net.zoneland.o2.view.CalendarViewEvent
import net.zoneland.o2.view.listener.OnEventClickListener
import net.zoneland.o2.view.listener.OnSchedulerPageChangedListener
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.calendar.vm.DayWeekViewModel
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.calendar.CalendarEventInfoData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.CalendarEventFilterVO
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.DateHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import java.util.*

/**
 * Created by fancyLou on 13/06/2018.
 * Copyright © 2018 O2. All rights reserved.
 */


class WeekCalendarViewFragment : CalendarBaseFragment(), OnSchedulerPageChangedListener {

    override fun layoutResId(): Int = R.layout.fragment_calendar_week

    private val viewModel: DayWeekViewModel by lazy { ViewModelProviders.of(this).get(DayWeekViewModel::class.java) }
    private val MY_FILTER_KEY = "MY_FILTER_KEY"
    private lateinit var myFilter: CalendarEventFilterVO

    override fun bindViewModel() {
        viewModel.currentFilter().observe(this, android.arch.lifecycle.Observer { cal ->
            updateTitle(cal?.start)
        })
        viewModel.getEventList().observe(this, android.arch.lifecycle.Observer { list->
            if (list!=null) {
                cv_calendar_week.removeEvents()
                cv_calendar_week.addEvents(list)
            }
        })
    }

    override fun initView() {
        if (!this::myFilter.isInitialized) {
            val day0 = DateHelper.getFirstDayOfWeek(Calendar.getInstance())
            val day6 = day0.clone() as Calendar
            day6.add(Calendar.DAY_OF_YEAR, 6)
            myFilter = CalendarEventFilterVO(
                    day0, day6, initCalendarIds()
            )
        }
        cv_calendar_week.setOnSchedulerPageChangedListener(this)
        cv_calendar_week.setOnEventClickListener(object : OnEventClickListener {
            override fun eventClick(event: CalendarViewEvent) {
                //open edit
                val data = event.mData
                if (data is CalendarEventInfoData) {
                    if (checkManageAble(data.manageablePersonList)) {
                        if (activity is CalendarMainActivity) {
                            (activity as CalendarMainActivity).editEvent(data)
                        }
                    }
                }
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val save = savedInstanceState?.getSerializable(MY_FILTER_KEY)
        if (save!=null){
            myFilter = save as CalendarEventFilterVO
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putSerializable(MY_FILTER_KEY, myFilter)
    }

    override fun onResume() {
        super.onResume()
        loadEvents()
    }

    override fun changed(showDays: List<Date>) {
        if (showDays.isNotEmpty()) {
            val day1 = Calendar.getInstance()
            day1.time = showDays.first()
            val day2 = Calendar.getInstance()
            day2.time = showDays.last()
            myFilter.start = day1
            myFilter.end = day2
        }
        loadEvents()
    }

    override fun setCalendarFilter(calendarIds: List<String>) {
        myFilter.calendarIds = calendarIds
        loadEvents()
    }

    override fun jump2Today() {
        cv_calendar_week.backToToday()
    }

    override fun updateTitle(cal: Calendar?) {
        if (cal != null ) {
            val month = DateHelper.getDateTime("yyyy年M月", cal.time)
            val week = cal.get(Calendar.WEEK_OF_YEAR)
            if (activity is CalendarMainActivity && isSelfShow) {
                (activity as CalendarMainActivity).updateActivityTitle("$month, 第${week}周")
            }
        } else {
            XLog.error("传入的显示日期为空！！！！！")
        }
    }
    override fun initCalendarIds(): List<String> {
        return if (activity is CalendarMainActivity) {
            (activity as CalendarMainActivity).getCalendarIds()
        }else {
            ArrayList()
        }
    }



    private fun loadEvents(){
        viewModel.filter(myFilter)
    }
}