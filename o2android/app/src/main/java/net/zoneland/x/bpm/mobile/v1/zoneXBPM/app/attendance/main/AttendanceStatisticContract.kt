package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.attendance.main

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.group.Group
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.attendance.AttendanceDetailInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.attendance.AttendanceStatisticGroupHeader

/**
 * Created by fancyLou on 28/05/2018.
 * Copyright © 2018 O2. All rights reserved.
 */


object AttendanceStatisticContract {
    interface View : BaseView {
        fun attendanceDetailList(result: List<Group<AttendanceStatisticGroupHeader, AttendanceDetailInfoJson>>, lateNumber: Int, earlierNumber: Int, absentNumber: Int, normalNumber: Int)
        fun attendanceStatisticCycle(result: String)
    }

    interface Presenter : BasePresenter<View> {
        fun getAttendanceListByMonth(month: String)
        fun getAttendanceStatisticCycle(month: String)
    }
}