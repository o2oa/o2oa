package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.attendance.main

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.attendance.AttendanceDetailInfoJson


object AttendanceChartContract {
    interface View : BaseView {
        fun attendanceDetailList(list: List<AttendanceDetailInfoJson>)
    }

    interface Presenter : BasePresenter<View> {
        fun  getAttendanceDetailList(year: String, month: String, distinguishedName: String)
    }
}
