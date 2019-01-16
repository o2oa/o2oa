package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.process

import android.text.TextUtils
import net.muliba.accounting.app.ExceptionHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2App
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.ResponseHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.TaskCompleteData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import okhttp3.MediaType
import okhttp3.RequestBody
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class TaskCompletedSearchPresenter : BasePresenterImpl<TaskCompletedSearchContract.View>(), TaskCompletedSearchContract.Presenter {

    override fun searchTaskCompleted(lastId: String, key: String) {
            if (TextUtils.isEmpty(lastId) || TextUtils.isEmpty(key)) {
                mView?.searchFail()
                XLog.error( "传入参数不正确！")
                return
            }
            val map = HashMap<String, String>()
            map.put("key", key)
            val json = O2SDKManager.instance().gson.toJson(map)
            XLog.debug("search json : $json")
            val body = RequestBody.create(MediaType.parse("text/json"), json)
            getProcessAssembleSurfaceServiceAPI(mView?.getContext())?.let { service->
                service.searchTaskCompleteListByPage(lastId, O2.DEFAULT_PAGE_NUMBER, body)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler<List<TaskCompleteData>>{list->mView?.searchResult(list)},
                            ExceptionHandler(mView?.getContext()){e->mView?.searchFail()})

        }
    }
}
