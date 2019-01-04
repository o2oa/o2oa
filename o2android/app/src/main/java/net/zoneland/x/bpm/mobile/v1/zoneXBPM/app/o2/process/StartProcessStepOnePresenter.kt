package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.process

import net.muliba.accounting.app.ExceptionHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.ResponseHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.ApplicationData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.ProcessInfoData
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class StartProcessStepOnePresenter : BasePresenterImpl<StartProcessStepOneContract.View>(), StartProcessStepOneContract.Presenter {

    override fun loadApplicationList() {
        getProcessAssembleSurfaceServiceAPI(mView?.getContext())?.let { service->
            service.getApplicationList()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler<List<ApplicationData>>({list-> mView?.loadApplicationList(list)}),
                            ExceptionHandler(mView?.getContext(), {e-> mView?.loadApplicationListFail()}))
        }
    }

    override fun loadProcessListByAppId(appId: String) {
        getProcessAssembleSurfaceServiceAPI(mView?.getContext())?.let { service->
            service.getApplicationProcess(appId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler<List<ProcessInfoData>>({list->mView?.loadProcessList(list)}),
                            ExceptionHandler(mView?.getContext(), {e->mView?.loadProcessListFail()}))
        }
    }
}
