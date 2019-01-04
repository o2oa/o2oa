package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.attendance.main

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.attendance.AttendanceDetailInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.attendance.AttendanceDetailQueryFilterJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class AttendanceChartPresenter : BasePresenterImpl<AttendanceChartContract.View>(), AttendanceChartContract.Presenter {

    override fun getAttendanceDetailList(year: String, month: String, distinguishedName: String) {
        XLog.debug("attendance chart query year$year, month:$month, person$distinguishedName")
        val filter = AttendanceDetailQueryFilterJson()
        filter.cycleYear = year
        filter.cycleMonth = month
        filter.key = "recordDateString"
        filter.order = "desc"
        filter.q_empName = distinguishedName
        getAttendanceAssembleControlService(mView?.getContext())?.let { service ->
            service.myAttendanceDetailChartList(filter)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .o2Subscribe {
                        onNext { response ->
                            if (response.data == null) {
                                mView?.attendanceDetailList(ArrayList<AttendanceDetailInfoJson>())
                            } else {
                                mView?.attendanceDetailList(response.data)
                            }

                        }
                        onError { e, _ ->
                            XLog.error("", e)
                            mView?.attendanceDetailList(ArrayList<AttendanceDetailInfoJson>())
                        }
                    }
        }
    }
}
