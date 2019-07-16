package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.cms.application

import android.text.TextUtils
import net.muliba.accounting.app.ExceptionHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2App
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.ResponseHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.cms.CMSDocumentInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import okhttp3.MediaType
import okhttp3.RequestBody
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class CMSCategoryPresenter : BasePresenterImpl<CMSCategoryContract.View>(), CMSCategoryContract.Presenter {

    override fun findDocumentByPage(id: String, lastId: String) {
        if (TextUtils.isEmpty(id) || TextUtils.isEmpty(lastId)) {
            mView?.loadFail()
            return
        }
        val wrapIn = HashMap<String, ArrayList<String>>()
        val category = ArrayList<String>()
        category.add(id)
        wrapIn["categoryIdList"] = category
        val status = ArrayList<String>()
        status.add("published")
        wrapIn["statusList"] = status
        val json = O2SDKManager.instance().gson.toJson(wrapIn)
        val body = RequestBody.create(MediaType.parse("text/json"), json)
        getCMSAssembleControlService(mView?.getContext())?.let { service ->
            service.filterDocumentList(body, lastId, O2.DEFAULT_PAGE_NUMBER)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler<List<CMSDocumentInfoJson>> { list -> mView?.loadSuccess(list) },
                            ExceptionHandler(mView?.getContext()) { e ->
                                XLog.error("", e)
                                mView?.loadFail()
                            })
        }
    }
}
