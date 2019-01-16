package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.process

import net.muliba.accounting.app.ExceptionHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.ResponseHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.TaskCompleteInfoDataWithControl
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by fancyLou on 02/05/2018.
 * Copyright © 2018 O2. All rights reserved.
 */


class TaskCompletedWorkListPresenter: BasePresenterImpl<TaskCompletedWorkListContract.View>(), TaskCompletedWorkListContract.Presenter {

    override fun loadTaskCompleteInfo(id: String) {
        getProcessAssembleSurfaceServiceAPI(mView?.getContext())?.let { service ->
            service.getTaskCompleteInfoWithControl(id).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler<TaskCompleteInfoDataWithControl> { task -> mView?.loadWorkCompletedInfo(task) },
                            ExceptionHandler(mView?.getContext(), { e -> mView?.loadWorkCompletedInfoFail() }))
        }
    }
}