package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.attendance.list

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.attendance.AttendanceDetailInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.attendance.AttendanceDetailQueryFilterJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class AttendanceListPresenter : BasePresenterImpl<AttendanceListContract.View>(), AttendanceListContract.Presenter {

    override fun getAppealableValue() {
        getAttendanceAssembleControlService(mView?.getContext())?.let { service ->
            service.getAppealableValue()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .o2Subscribe {
                        onNext { response ->
                            val setting = response.data
                             if (setting!=null && setting.configValue.equals(O2.ATTENDANCE_SETTING_APPEAL_ABLE_TRUE)) {
                                mView?.appealAble(true)
                            }else {
                                mView?.appealAble(false)
                            }
                        }
                        onError { e, _ ->
                            XLog.error("", e)
                            mView?.appealAble(false)
                        }
                    }
        }
    }

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
                            if (response.data == null){
                                mView?.attendanceDetailList(ArrayList<AttendanceDetailInfoJson>())
                            }else {
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
