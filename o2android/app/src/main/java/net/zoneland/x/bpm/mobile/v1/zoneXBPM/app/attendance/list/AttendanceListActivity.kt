package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.attendance.list


import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.widget.ImageView
import kotlinx.android.synthetic.main.content_attendance.*
import kotlinx.android.synthetic.main.snippet_shimmer_content.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2App
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.attendance.appeal.AttendanceAppealActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecycleViewAdapter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecyclerViewHolder
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.enums.AttendanceStatus
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.attendance.AttendanceDetailInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.DateHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.go
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.gone
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.inVisible
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.DividerItemDecoration


class AttendanceListActivity : BaseMVPActivity<AttendanceListContract.View, AttendanceListContract.Presenter>(), AttendanceListContract.View {
    override var mPresenter: AttendanceListContract.Presenter = AttendanceListPresenter()

    override fun layoutResId(): Int = R.layout.activity_attendance
    var appealAble = false
    val itemList = ArrayList<AttendanceDetailInfoJson>()
    val adapter: CommonRecycleViewAdapter<AttendanceDetailInfoJson> by lazy {
        object : CommonRecycleViewAdapter<AttendanceDetailInfoJson>(this, itemList, R.layout.item_attendance_detail_list) {
            override fun convert(holder: CommonRecyclerViewHolder?, info: AttendanceDetailInfoJson?) {
                if (holder!=null && info!=null) {
                    val appealIcon = holder.getView<ImageView>(R.id.image_attendance_detail_list_item_appeal_icon)
                    appealIcon?.inVisible()
                    var appealStatus = ""
                    if (appealAble) {
                        when (info.appealStatus) {
                            0 -> {
                                if (!info.isGetSelfHolidays && (info.isAbsent || info.isLate || info.isAbnormalDuty || info.isLackOfTime)) {
                                    appealIcon?.visible()
                                }
                            }
                            9 -> appealStatus = "申诉通过"
                            1 -> appealStatus = "申诉中"
                            -1 -> appealStatus = "申诉未通过"
                        }
                    }
                    var off_on_time = ""
                    when {
                        TextUtils.isEmpty(info.onDutyTime) && TextUtils.isEmpty(info.offDutyTime) -> off_on_time = ""
                        TextUtils.isEmpty(info.onDutyTime) && !TextUtils.isEmpty(info.offDutyTime) -> off_on_time = info.offDutyTime
                        !TextUtils.isEmpty(info.onDutyTime) && TextUtils.isEmpty(info.offDutyTime) -> off_on_time = info.onDutyTime
                        !TextUtils.isEmpty(info.onDutyTime) && !TextUtils.isEmpty(info.offDutyTime) -> off_on_time = "${info.onDutyTime} - ${info.offDutyTime}"
                    }
                    var desc = "工作日"
                    when {
                        info.isHoliday -> desc = "节假日"
                        info.isWeekend -> desc = "周末"
                        info.isWorkday -> desc = "调休工作日"
                    }
                    var status = AttendanceStatus.NORMAL.label
                    when {
                        info.isGetSelfHolidays -> status = AttendanceStatus.HOLIDAY.label
                        info.isLate -> status = AttendanceStatus.LATE.label
                        info.isAbsent -> status = AttendanceStatus.ABSENT.label
                        info.isAbnormalDuty -> status = AttendanceStatus.ABNORMALDUTY.label
                        info.isLackOfTime -> status = AttendanceStatus.LACKOFTIME.label
                    }
                    if (!TextUtils.isEmpty(appealStatus)) {
                        if (!TextUtils.isEmpty(info.appealProcessor)) {
                            status = "$status ($appealStatus, 审核人：${info.appealProcessor})"
                        }else {
                            status = "$status ($appealStatus)"
                        }
                    }
                    XLog.debug("审批人： ${info.appealProcessor}")
                    holder.setText(R.id.tv_attendance_detail_list_item_day, info.recordDateString)
                            .setText(R.id.tv_attendance_detail_list_item_on_off_duty_time, off_on_time)
                            .setText(R.id.tv_attendance_detail_list_item_desc, desc)
                            .setText(R.id.tv_attendance_detail_list_item_status, status)
                }
            }
        }
    }

    override fun afterSetContentView(savedInstanceState: Bundle?) {
        setupToolBar(getString(R.string.title_activity_attendance), true)
        swipe_refresh_content.setColorSchemeResources(R.color.z_color_refresh_scuba_blue,
                R.color.z_color_refresh_red, R.color.z_color_refresh_purple, R.color.z_color_refresh_orange)
        swipe_refresh_content.setOnRefreshListener {
            mPresenter.getAttendanceDetailList(DateHelper.nowByFormate("yyyy"), DateHelper.nowByFormate("MM"), O2SDKManager.instance().distinguishedName)
        }
        recycler_attendance_detail_list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recycler_attendance_detail_list.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST))
        recycler_attendance_detail_list.adapter = adapter
        adapter.setOnItemClickListener { _, position ->
            if (appealAble ) {
                val detail = itemList[position]
                if (detail.appealStatus == 0) {
                    if (!detail.isGetSelfHolidays && (detail.isAbsent || detail.isLate || detail.isAbnormalDuty || detail.isLackOfTime)) {
                        XLog.debug("start attendance appeal ")
                        go<AttendanceAppealActivity>(AttendanceAppealActivity.startBundleData(detail))
                    }
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        swipe_refresh_content.isRefreshing = true
        mPresenter.getAppealableValue()
        mPresenter.getAttendanceDetailList(DateHelper.nowByFormate("yyyy"), DateHelper.nowByFormate("MM"), O2SDKManager.instance().distinguishedName)
    }

    override fun attendanceDetailList(list: List<AttendanceDetailInfoJson>) {
        swipe_refresh_content.isRefreshing = false
        shimmer_snippet_content.gone()
        itemList.clear()
        if (list.isEmpty()) {
            tv_attendance_detail_no_data.visible()
            swipe_refresh_content.gone()
        }else {
            swipe_refresh_content.visible()
            tv_attendance_detail_no_data.gone()
            itemList.addAll(list)
        }
        adapter.notifyDataSetChanged()
    }

    override fun appealAble(flag: Boolean) {
        appealAble = flag
        adapter.notifyDataSetChanged()
    }
}
