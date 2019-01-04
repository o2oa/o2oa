package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.attendance.setting

import android.text.TextUtils
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2App
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.attendance.MobileCheckInWorkplaceInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
import okhttp3.MediaType
import okhttp3.RequestBody
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class AttendanceLocationSettingPresenter : BasePresenterImpl<AttendanceLocationSettingContract.View>(), AttendanceLocationSettingContract.Presenter {

    override fun deleteWorkplace(id: String) {
        getAttendanceAssembleControlService(mView?.getContext())?.let { service ->
            service.deleteAttendanceWorkplace(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .o2Subscribe {
                        onNext {
                            response ->
                            XLog.debug("$response")
                            mView?.deleteWorkplace(true)
                        }
                        onError {
                            e, _ ->
                            XLog.error("", e)
                            mView?.deleteWorkplace(false)
                        }
                    }
        }
    }

    override fun saveWorkplace(name: String, errorRange: String, latitude: String, longitude: String) {
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(latitude) || TextUtils.isEmpty(longitude)) {
                mView?.saveWorkplace(false)
                return
            }
            val info = MobileCheckInWorkplaceInfoJson()
            info.placeName = name
            info.errorRange = if (TextUtils.isEmpty(errorRange)){100}else{errorRange.toInt()}
            info.latitude = latitude
            info.longitude = longitude
            val json = O2SDKManager.instance().gson.toJson(info)
            val body = RequestBody.create(MediaType.parse("text/json"), json)
            getAttendanceAssembleControlService(mView?.getContext())?.let { service ->
                service.attendanceWorkplace(body)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .o2Subscribe {
                        onNext { response ->
                            XLog.debug("$response")
                            mView?.saveWorkplace(true)
                        }
                        onError { e, _ ->
                            XLog.error("",e)
                            mView?.saveWorkplace(false)
                        }
                    }

        }
    }

    override fun loadAllWorkplace() {
        getAttendanceAssembleControlService(mView?.getContext())?.let { service ->
            service.findAllWorkplace().subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .o2Subscribe {
                        onNext { response ->
                            mView?.workplaceList(response.data?: ArrayList<MobileCheckInWorkplaceInfoJson>())
                        }
                        onError { e, _ ->
                            XLog.error("",e)
                            mView?.workplaceList(ArrayList<MobileCheckInWorkplaceInfoJson>())
                        }
                    }
        }
    }
}
