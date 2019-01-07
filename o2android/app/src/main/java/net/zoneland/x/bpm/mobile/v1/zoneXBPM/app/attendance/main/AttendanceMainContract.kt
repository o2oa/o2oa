package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.attendance.main

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView


object AttendanceMainContract {
    interface View : BaseView {
        fun isAttendanceAdmin(flag: Boolean)
    }

    interface Presenter : BasePresenter<View> {
        fun loadAttendanceAdmin()
    }
}
