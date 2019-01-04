package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.attendance.approval

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.attendance.AppealInfoJson


object AttendanceAppealApprovalContract {
    interface View : BaseView {
        fun attendanceAppealList(list: List<AppealInfoJson>)
        fun approvalAppealFinish()
    }

    interface Presenter : BasePresenter<View> {
        fun approvalAppeal(mSelectedSet: HashSet<String>, agree: Boolean)
        fun  findAttendanceAppealInfoListByPage(lastId: String)
    }
}
