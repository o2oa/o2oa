package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.attendance.list

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.attendance.AttendanceDetailInfoJson


object AttendanceListContract {
    interface View : BaseView {
        fun attendanceDetailList(list: List<AttendanceDetailInfoJson>)
        fun appealAble(flag: Boolean)
    }

    interface Presenter : BasePresenter<View> {
        fun getAppealableValue()
        fun getAttendanceDetailList(year: String, month: String, distinguishedName: String)
    }
}
