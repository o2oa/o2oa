package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.attendance.appeal

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2App
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.ApiResponse
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.attendance.AttendanceDetailInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.identity.IdentityJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class AttendanceAppealPresenter : BasePresenterImpl<AttendanceAppealContract.View>(), AttendanceAppealContract.Presenter {

    override fun getMyIdentity() {
        getAttendanceAssembleControlService(mView?.getContext())?.let { service ->
            service.getAppealAuditorType().flatMap { response ->
                val info = response.data
                XLog.debug("config:$info")
                if (info != null && info.configValue == (O2.ATTENDANCE_SETTING_AUDITOR_TYPE_NEED_CHOOSE_IDENTITY)) {
                    getOrganizationAssembleControlApi(mView?.getContext())?.identityListWithPerson(O2SDKManager.instance().distinguishedName)
                } else {
                    Observable.just(ApiResponse<List<IdentityJson>>())
                }
            }.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .o2Subscribe {
                        onNext { response ->
                            val identities = response.data
                            if (identities != null && identities.size > 1) {//小于等于1个身份  都不需要显示选择身份的选项
                                mView?.myIdentity(identities)
                            } else {
                                mView?.myIdentity(ArrayList<IdentityJson>())
                            }
                        }
                        onError { e, _ ->
                            XLog.error("", e)
                            mView?.myIdentity(ArrayList<IdentityJson>())
                        }
                    }
        }
    }

    override fun submitAppeal(info: AttendanceDetailInfoJson) {
        getAttendanceAssembleControlService(mView?.getContext())?.let { service ->
            service.submitAppeal(info, info.id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .o2Subscribe {
                        onNext { response ->
                            XLog.debug("${response.data}")
                            mView?.submitAppeal(true)
                        }
                        onError { e, _ ->
                            XLog.error("", e)
                            mView?.submitAppeal(false)
                        }
                    }
        }
    }


}
