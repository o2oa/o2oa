package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.process

import android.text.TextUtils
import net.muliba.accounting.app.ExceptionHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.ResponseHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.ReadCompleteData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class ReadCompletedListPresenter : BasePresenterImpl<ReadCompletedListContract.View>(), ReadCompletedListContract.Presenter {

    override fun findReadCompletedList(applicationId: String, lastId: String, limit: Int) {
            if (TextUtils.isEmpty(applicationId) || TextUtils.isEmpty(lastId)) {
                mView?.finishLoading()
               XLog.error( "传入参数不正确！")
                return
            }
            getProcessAssembleSurfaceServiceAPI(mView?.getContext())?.let { service->
                service.getReadCompleteListByPage(lastId, limit)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler<List<ReadCompleteData>>{list->mView?.returnReadCompletedList(list)},
                            ExceptionHandler(mView?.getContext()){e->mView?.finishLoading()})
        }
    }
}
