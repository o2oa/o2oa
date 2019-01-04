package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.calendar

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import kotlinx.android.synthetic.main.fragment_calendar_day.*
import net.zoneland.o2.view.CalendarViewEvent
import net.zoneland.o2.view.listener.OnEventClickListener
import net.zoneland.o2.view.listener.OnSchedulerPageChangedListener
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.calendar.vm.DayWeekViewModel
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.calendar.CalendarEventInfoData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.calendar.CalendarInfoData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.CalendarEventFilterVO
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.DateHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by fancyLou on 13/06/2018.
 * Copyright © 2018 O2. All rights reserved.
 */

class DayCalendarViewFragment : CalendarBaseFragment(), OnSchedulerPageChangedListener {

    override fun layoutResId(): Int = R.layout.fragment_calendar_day
    private val MY_FILTER_KEY = "MY_FILTER_KEY"
    private lateinit var myFilter: CalendarEventFilterVO
    private val viewModel: DayWeekViewModel by lazy { ViewModelProviders.of(this).get(DayWeekViewModel::class.java) }


    override fun bindViewModel() {
        viewModel.currentFilter().observe(this, android.arch.lifecycle.Observer { filter->
            updateTitle(filter?.start)
        })
        viewModel.getEventList().observe(this, android.arch.lifecycle.Observer {  list->
            if (list!=null) {
                cv_calendar_day.removeEvents()
                cv_calendar_day.addEvents(list)
            }
        })
    }

    override fun initView() {
        if (!this::myFilter.isInitialized) {
            val now = Calendar.getInstance()
            myFilter = CalendarEventFilterVO(
                    now, now, initCalendarIds()
            )
        }
        cv_calendar_day.setOnSchedulerPageChangedListener(this)
        cv_calendar_day.setOnEventClickListener(object : OnEventClickListener {
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
            val day = Calendar.getInstance()
            day.time = showDays.first()
            myFilter.start = day
            myFilter.end = day
        }
        loadEvents()
    }

    override fun setCalendarFilter(calendarIds: List<String>) {
        myFilter.calendarIds = calendarIds
        loadEvents()
    }

    override fun jump2Today() {
        cv_calendar_day.backToToday()
    }

    override fun updateTitle(cal: Calendar?) {
        if (cal != null ) {
            val day = DateHelper.getDateTime("yyyy年M月d日", cal.time)
            if (activity is CalendarMainActivity && isSelfShow) {
                (activity as CalendarMainActivity).updateActivityTitle(day)
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
