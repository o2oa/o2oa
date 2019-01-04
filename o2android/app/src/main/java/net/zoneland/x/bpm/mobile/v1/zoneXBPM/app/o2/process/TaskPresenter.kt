package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.process

import net.muliba.accounting.app.ExceptionHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.ResponseHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.TaskData
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class TaskPresenter : BasePresenterImpl<TaskContract.View>(), TaskContract.Presenter {

    override fun findTaskList(applicationId: String, lastId: String, limit: Int) {
        getProcessAssembleSurfaceServiceAPI(mView?.getContext())?.let { service->
            if (applicationId == "-1") {
                service.getTaskListByPage(lastId, limit)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(ResponseHandler<List<TaskData>>{list->mView?.findTaskList(list)},
                                ExceptionHandler(mView?.getContext()){e->mView?.findTaskListFail()})
            }else {
                service.getTaskListByPageWithApplication(lastId, limit, applicationId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(ResponseHandler<List<TaskData>>{list->mView?.findTaskList(list)},
                                ExceptionHandler(mView?.getContext()){e->mView?.findTaskListFail()})
            }
        }
    }
}
