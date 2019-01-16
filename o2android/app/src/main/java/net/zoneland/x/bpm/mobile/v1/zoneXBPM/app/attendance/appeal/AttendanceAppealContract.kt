package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.attendance.appeal

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.attendance.AttendanceDetailInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.identity.IdentityJson


object AttendanceAppealContract {
    interface View : BaseView {
        fun submitAppeal(flag: Boolean)
        fun myIdentity(list: List<IdentityJson>)
    }

    interface Presenter : BasePresenter<View> {
        fun  submitAppeal(info: AttendanceDetailInfoJson)
        fun getMyIdentity()
    }
}
