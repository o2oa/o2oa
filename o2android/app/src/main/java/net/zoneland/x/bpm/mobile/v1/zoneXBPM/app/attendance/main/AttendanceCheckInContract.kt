package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.attendance.main

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.attendance.MobileCheckInJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.attendance.MobileCheckInWorkplaceInfoJson


object AttendanceCheckInContract {
    interface View : BaseView {

        fun workplaceList(list: List<MobileCheckInWorkplaceInfoJson>)
        fun todayCheckInRecord(list: List<MobileCheckInJson>)
        fun checkIn(result: Boolean)
    }

    interface Presenter : BasePresenter<View> {

        fun findTodayCheckInRecord(cId: String)
        fun loadAllWorkplace()
        fun checkIn(latitude: String, longitude: String, addrStr: String?, signDesc: String, signDate: String, signTime: String, id: String)
    }
}
