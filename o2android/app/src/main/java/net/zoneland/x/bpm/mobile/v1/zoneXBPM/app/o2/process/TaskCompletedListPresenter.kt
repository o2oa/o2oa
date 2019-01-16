package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.process

import net.muliba.accounting.app.ExceptionHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.ResponseHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.TaskApplicationData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.TaskCompleteInfoData
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class TaskCompletedListPresenter : BasePresenterImpl<TaskCompletedListContract.View>(), TaskCompletedListContract.Presenter {

    override fun findTaskCompletedApplicationList() {
        getProcessAssembleSurfaceServiceAPI(mView?.getContext())?.let { service->
            service.getTaskApplicationList()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler<List<TaskApplicationData>> { list -> mView?.findTaskCompletedApplicationList(list) },
                            ExceptionHandler(mView?.getContext()) { e -> mView?.findTaskCompletedApplicationListFail() })
        }
    }




}
