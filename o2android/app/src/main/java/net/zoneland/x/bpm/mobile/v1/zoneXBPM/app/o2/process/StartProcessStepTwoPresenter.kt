package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.process

import android.text.TextUtils
import net.muliba.accounting.app.ExceptionHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.ResponseHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.identity.ProcessWOIdentityJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.ProcessStartBo
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.ProcessWorkData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class StartProcessStepTwoPresenter : BasePresenterImpl<StartProcessStepTwoContract.View>(), StartProcessStepTwoContract.Presenter {

    override fun loadCurrentPersonIdentityWithProcess(processId: String) {
        getProcessAssembleSurfaceServiceAPI(mView?.getContext())?.let { service->
            service.availableIdentityWithProcess(processId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler<List<ProcessWOIdentityJson>>({ list ->
                        XLog.debug("identities: $list")
                        mView?.loadCurrentPersonIdentity(list)
                    }),
                            ExceptionHandler(mView?.getContext(), { e -> mView?.loadCurrentPersonIdentityFail() }))
        }
    }

    override fun startProcess(title: String, identity: String, processId: String) {
            if (TextUtils.isEmpty(title) || TextUtils.isEmpty(identity) || TextUtils.isEmpty(processId)) {
                mView?.startProcessFail("传入参数为空，无法启动流程，title:$title, identity:$identity,processId:$processId")
                return
            }
            val body = ProcessStartBo()
            body.title = title
            body.identity = identity
            getProcessAssembleSurfaceServiceAPI(mView?.getContext())?.let { service->
                service.startProcess(processId, body)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler<List<ProcessWorkData>>({ list ->
                        try {
                            mView?.startProcessSuccess(list[0].taskList[0].work)
                        } catch (e: Exception) {
                            XLog.error("", e)
                            mView?.startProcessFail("返回数据异常！${e.message}")
                        }
                    }), ExceptionHandler(mView?.getContext(), { e ->
                        mView?.startProcessFail(e.message ?: "")
                    }))
        }
    }
}
