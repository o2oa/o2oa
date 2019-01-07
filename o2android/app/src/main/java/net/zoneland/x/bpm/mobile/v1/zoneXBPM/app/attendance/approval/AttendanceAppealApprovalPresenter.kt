package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.attendance.approval

import android.text.TextUtils
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2App
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.attendance.AppealApprovalFormJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.attendance.AppealApprovalQueryFilterJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.attendance.AppealInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.DateHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class AttendanceAppealApprovalPresenter : BasePresenterImpl<AttendanceAppealApprovalContract.View>(), AttendanceAppealApprovalContract.Presenter {

    override fun approvalAppeal(mSelectedSet: HashSet<String>, agree: Boolean) {
        XLog.debug("submit set size:${mSelectedSet.size}")
        if (mSelectedSet.isEmpty()) {
            mView?.approvalAppealFinish()
            return
        }

        val form = AppealApprovalFormJson()
        form.ids = ArrayList(mSelectedSet.map { it })
        form.status = if (agree) "1" else "-1"
        XLog.debug("$form")
        getAttendanceAssembleControlService(mView?.getContext())?.let { service ->
            service.approvalAppealInfo(form)
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .o2Subscribe {
                        onNext { response ->
                            val back = response.data
                            XLog.debug("$back")
                            mView?.approvalAppealFinish()
                        }
                        onError { e, _ ->
                            XLog.error("", e)
                            mView?.approvalAppealFinish()
                        }
                    }
        }
    }

    override fun findAttendanceAppealInfoListByPage(lastId: String) {
        var variableId = lastId
        if (TextUtils.isEmpty(lastId)) {
            variableId = O2.FIRST_PAGE_TAG
        }
        val filter = AppealApprovalQueryFilterJson()
        filter.status = "0"//未处理的
        filter.processPerson1 = O2SDKManager.instance().distinguishedName
        filter.yearString = DateHelper.nowByFormate("yyyy")
        getAttendanceAssembleControlService(mView?.getContext())?.let { service ->
            service.findAttendanceAppealInfoListByPage(variableId, O2.DEFAULT_PAGE_NUMBER, filter)
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .o2Subscribe {
                        onNext { response ->
                            mView?.attendanceAppealList(response.data ?: ArrayList<AppealInfoJson>())
                        }
                        onError { e, _ ->
                            XLog.error("", e)
                            mView?.attendanceAppealList(ArrayList<AppealInfoJson>())
                        }
                    }
        }
    }
}
