package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.process

import android.text.TextUtils
import net.muliba.accounting.app.ExceptionHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.ResponseHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.ReadData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class ReadListPresenter : BasePresenterImpl<ReadListContract.View>(), ReadListContract.Presenter {

    override fun findReadList(applicationId: String, lastId: String, limit: Int) {

            if (TextUtils.isEmpty(applicationId) || TextUtils.isEmpty(lastId)) {
                mView?.finishLoading()
                XLog.error("传入参数不正确！")
                return
            }
            getProcessAssembleSurfaceServiceAPI(mView?.getContext())?.let { service->
                service.getReadListByPage(lastId, limit)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler<List<ReadData>>{list->mView?.returnReadList(list)},
                            ExceptionHandler(mView?.getContext()){e->mView?.finishLoading()})
        }
    }
}
