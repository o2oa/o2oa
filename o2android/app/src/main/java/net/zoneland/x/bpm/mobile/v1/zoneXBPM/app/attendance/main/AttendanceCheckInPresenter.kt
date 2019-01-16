package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.attendance.main

import android.text.TextUtils
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2App
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.attendance.MobileCheckInJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.attendance.MobileCheckInQueryFilterJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.AndroidUtils
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.DateHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
import okhttp3.MediaType
import okhttp3.RequestBody
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class AttendanceCheckInPresenter : BasePresenterImpl<AttendanceCheckInContract.View>(), AttendanceCheckInContract.Presenter {


    override fun findTodayCheckInRecord(person: String) {
        val queryBean = MobileCheckInQueryFilterJson()
        queryBean.empName = person
        queryBean.startDate = DateHelper.nowByFormate("yyyy-MM-dd")
        val json = O2SDKManager.instance().gson.toJson(queryBean)
        val body = RequestBody.create(MediaType.parse("text/json"), json)
        getAttendanceAssembleControlService(mView?.getContext())?.let { service ->
            service.findAttendanceDetailMobileByPage(body, 1, 100)
                    .subscribeOn(Schedulers.io())
                    .flatMap { response->
                        val list = response.data
                        val retList = list?.sortedByDescending { it.signTime } ?: ArrayList()
                        Observable.just(retList)
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .o2Subscribe {
                        onNext { list ->
                            mView?.todayCheckInRecord(list)
                        }
                        onError { e, _ ->
                            XLog.error("", e)
                            mView?.todayCheckInRecord(ArrayList())
                        }
                    }
        }
    }

    override fun loadAllWorkplace() {
        getAttendanceAssembleControlService(mView?.getContext())?.let { service ->
            service.findAllWorkplace()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .o2Subscribe {
                        onNext { response ->
                            mView?.workplaceList(response.data)
                        }
                        onError { e, _ ->
                            XLog.error("", e)
                            mView?.workplaceList(ArrayList())
                        }
                    }
        }
    }

    override fun checkIn(latitude: String, longitude: String, addrStr: String?, signDesc: String, signDate: String, signTime: String, id: String) {
        val form = MobileCheckInJson()
        if (!TextUtils.isEmpty(id)) {
            form.id = id
        }
        form.signDescription = signDesc
        form.latitude = latitude
        form.longitude = longitude
        form.recordDateString = signDate
        form.signTime = signTime
        form.optMachineType = AndroidUtils.getDeviceBrand() + "-" + AndroidUtils.getDeviceModelNumber()
        form.optSystemName = O2.DEVICE_TYPE
        form.recordAddress = addrStr ?: ""
        val json = O2SDKManager.instance().gson.toJson(form)
        val body = RequestBody.create(MediaType.parse("text/json"), json)
        getAttendanceAssembleControlService(mView?.getContext())?.let { service ->
            service.attendanceDetailCheckIn(body)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .o2Subscribe {
                        onNext { _ ->
                            mView?.checkIn(true)
                        }
                        onError { e, _ ->
                            XLog.error("", e)
                            mView?.checkIn(false)
                        }
                    }
        }
    }
}
