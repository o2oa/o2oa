package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.attendance.main

import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import com.jzxiang.pickerview.TimePickerDialog
import com.jzxiang.pickerview.data.Type
import com.jzxiang.pickerview.listener.OnDateSetListener
import kotlinx.android.synthetic.main.fragment_attendance_statistic.*
import net.muliba.changeskin.FancySkinManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPViewPagerFragment
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecyclerViewHolder
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.group.Group
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.group.GroupRecyclerViewAdapter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.attendance.AttendanceDetailInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.attendance.AttendanceStatisticGroupHeader
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.DateHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.CircleTextView
import java.util.*

/**
 * Created by fancyLou on 28/05/2018.
 * Copyright © 2018 O2. All rights reserved.
 */


class AttendanceStatisticFragment : BaseMVPViewPagerFragment<AttendanceStatisticContract.View, AttendanceStatisticContract.Presenter>(),
        AttendanceStatisticContract.View, OnDateSetListener {
    override var mPresenter: AttendanceStatisticContract.Presenter = AttendanceStatisticPresenter()


    override fun layoutResId(): Int = R.layout.fragment_attendance_statistic

    private val list: ArrayList<Group<AttendanceStatisticGroupHeader, AttendanceDetailInfoJson>> = ArrayList()
    private val adapter: GroupRecyclerViewAdapter<AttendanceStatisticGroupHeader, AttendanceDetailInfoJson> by lazy {
        object : GroupRecyclerViewAdapter<AttendanceStatisticGroupHeader, AttendanceDetailInfoJson>(list, R.layout.item_attendance_statistic_header, R.layout.item_attendance_statistic_body) {
            override fun onBindHeaderViewHolder(holder: CommonRecyclerViewHolder, header: AttendanceStatisticGroupHeader, position: Int) {
                when (header.groupType) {
                    0 -> {
                        val headerType = holder.getView<CircleTextView>(R.id.circle_tv_item_attendance_statistic_header_type)
                        headerType.setTextAndCircleColor("迟", ContextCompat.getColor(activity, R.color.z_attendance_late))
                    }
                    1 -> {
                        val headerType = holder.getView<CircleTextView>(R.id.circle_tv_item_attendance_statistic_header_type)
                        headerType.setTextAndCircleColor("早", ContextCompat.getColor(activity, R.color.z_attendance_leaveEarlier))
                    }
                    2 -> {
                        val headerType = holder.getView<CircleTextView>(R.id.circle_tv_item_attendance_statistic_header_type)
                        headerType.setTextAndCircleColor("缺", ContextCompat.getColor(activity, R.color.z_attendance_absent))
                    }
                    3 -> {
                        val headerType = holder.getView<CircleTextView>(R.id.circle_tv_item_attendance_statistic_header_type)
                        headerType.setTextAndCircleColor("正", ContextCompat.getColor(activity, R.color.z_color_primary))
                    }
                }
                var weekDay = DateHelper.getWeekDay(header.firstDetail.recordDateString, "yyyy-MM-dd")
                weekDay = if (weekDay != null) {
                    "($weekDay)"
                } else {
                    ""
                }
                holder.setText(R.id.tv_item_attendance_statistic_header_date, header.firstDetail.recordDateString)
                        .setText(R.id.tv_item_attendance_statistic_header_weekday, weekDay)
                        .setText(R.id.tv_item_attendance_statistic_header_on_work_time, header.firstDetail.onDutyTime)
                        .setText(R.id.tv_item_attendance_statistic_header_off_work_time, header.firstDetail.offDutyTime)
            }

            override fun onBindChildViewHolder(holder: CommonRecyclerViewHolder, child: AttendanceDetailInfoJson, position: Int) {
                var weekDay = DateHelper.getWeekDay(child.recordDateString, "yyyy-MM-dd")
                weekDay = if (weekDay != null) {
                    "($weekDay)"
                } else {
                    ""
                }
                holder.setText(R.id.tv_item_attendance_statistic_body_date, child.recordDateString)
                        .setText(R.id.tv_item_attendance_statistic_body_weekday, weekDay)
                        .setText(R.id.tv_item_attendance_statistic_body_on_work_time, child.onDutyTime)
                        .setText(R.id.tv_item_attendance_statistic_body_off_work_time, child.offDutyTime)
            }
        }
    }

    private var month = DateHelper.nowByFormate("yyyy-MM")


    override fun initUI() {
        recycler_attendance_statistic_list.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recycler_attendance_statistic_list.adapter = adapter
        tv_attendance_statistic_month.text = month
        rl_attendance_statistic_choose_month_btn.setOnClickListener {
            showMonthPicker()
        }
    }

    override fun lazyLoad() {
        loadData()
    }


    override fun attendanceDetailList(result: List<Group<AttendanceStatisticGroupHeader, AttendanceDetailInfoJson>>, lateNumber: Int, earlierNumber: Int, absentNumber: Int, normalNumber: Int) {
        circle_attendance_statistic_normal.setText("${normalNumber}天")
        circle_attendance_statistic_late.setText("${lateNumber}次")
        circle_attendance_statistic_leave_earlier.setText("${earlierNumber}次")
        circle_attendance_statistic_absent.setText("${absentNumber}次")
        list.clear()
        list.addAll(result)
        adapter.notifyDataSetChanged()
    }

    override fun attendanceStatisticCycle(result: String) {
        tv_attendance_statistic_period.text = result
    }

    override fun onDateSet(timePickerView: TimePickerDialog?, millseconds: Long) {
        val newMonth = DateHelper.getDateTime("yyyy-MM", Date(millseconds))
        if (!TextUtils.isEmpty(newMonth)) {
            month = newMonth
            tv_attendance_statistic_month.text = month
        }
        loadData()
    }

    private fun loadData() {
        mPresenter.getAttendanceStatisticCycle(month)
        mPresenter.getAttendanceListByMonth(month)
    }

    private fun showMonthPicker() {
        val currentMonth = DateHelper.convertStringToDate("yyyy-MM", month).time
        val monthChooser = TimePickerDialog.Builder()
                .setThemeColor(FancySkinManager.instance().getColor(activity, R.color.z_color_primary))
                .setType(Type.YEAR_MONTH)
                .setTitleStringId("月份选择")
                .setCurrentMillseconds(currentMonth)
                .setCallBack(this)
                .build()
        monthChooser.show(activity.supportFragmentManager, "month")

    }
}