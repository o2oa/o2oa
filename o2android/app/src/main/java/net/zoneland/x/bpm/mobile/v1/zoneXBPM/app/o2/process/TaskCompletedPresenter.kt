package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.process

import net.muliba.accounting.app.ExceptionHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.ResponseHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.TaskCompleteData
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class TaskCompletedPresenter : BasePresenterImpl<TaskCompletedContract.View>(), TaskCompletedContract.Presenter {

    override fun findTaskCompletedList(applicationId: String, lastId: String, limit: Int) {
        getProcessAssembleSurfaceServiceAPI(mView?.getContext())?.let { service->
            if (applicationId.equals("-1")) {
                service.getTaskCompleteListByPage(lastId, limit)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(ResponseHandler<List<TaskCompleteData>>{ list->mView?.findTaskCompletedList(list)},
                                ExceptionHandler(mView?.getContext()){e->mView?.findTaskCompletedListFail()})
            }else {
                service.getTaskCompleteListByPageWithApplication(lastId, limit, applicationId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(ResponseHandler<List<TaskCompleteData>>{ list->mView?.findTaskCompletedList(list)},
                                ExceptionHandler(mView?.getContext()){e->mView?.findTaskCompletedListFail()})
            }
        }
    }
}
