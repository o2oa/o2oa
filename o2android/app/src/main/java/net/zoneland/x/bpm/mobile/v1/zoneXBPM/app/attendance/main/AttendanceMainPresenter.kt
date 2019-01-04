package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.attendance.main

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2App
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class AttendanceMainPresenter : BasePresenterImpl<AttendanceMainContract.View>(), AttendanceMainContract.Presenter {

    override fun loadAttendanceAdmin() {
        getAttendanceAssembleControlService(mView?.getContext())?.let { service ->
            service.attendanceAdmin()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .o2Subscribe {
                        onNext { response ->
                            var flag = false
                            val list = response.data
                            list?.filter { O2SDKManager.instance().distinguishedName.equals(it.adminName) }?.map { flag = true }
                            mView?.isAttendanceAdmin(flag)
                        }
                        onError { e, _ ->
                            XLog.error("", e)
                            mView?.isAttendanceAdmin(false)
                        }
                    }
        }
    }

}
