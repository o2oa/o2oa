package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.attendance.setting

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.attendance.MobileCheckInWorkplaceInfoJson


object AttendanceLocationSettingContract {
    interface View : BaseView {
        fun deleteWorkplace(flag: Boolean)
        fun saveWorkplace(flag: Boolean)
        fun workplaceList(list: List<MobileCheckInWorkplaceInfoJson>)
    }

    interface Presenter : BasePresenter<View> {
        fun deleteWorkplace(id: String)
        fun saveWorkplace(name: String, errorRange: String, latitude: String, longitude: String)
        fun loadAllWorkplace()
    }
}
