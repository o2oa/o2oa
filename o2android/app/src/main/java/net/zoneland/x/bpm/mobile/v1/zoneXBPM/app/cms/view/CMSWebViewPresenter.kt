package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.cms.view

import net.muliba.accounting.app.ExceptionHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.ResponseHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.cms.CMSDocumentAttachmentJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.AttachmentItemVO
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class CMSWebViewPresenter : BasePresenterImpl<CMSWebViewContract.View>(), CMSWebViewContract.Presenter {

    override fun loadAttachList(docId: String) {
        getCMSAssembleControlService(mView?.getContext())?.let { service->
            service.getDocumentAttachList(docId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler<List<CMSDocumentAttachmentJson>> { list ->
                        val attachList = ArrayList<AttachmentItemVO>()
                        list.map { attachList.add(it.copyToVO()) }
                        mView?.loadAttachList(attachList)
                    }, ExceptionHandler(mView?.getContext()) { e ->
                        XLog.error("", e)
                    })
        }
    }
}
