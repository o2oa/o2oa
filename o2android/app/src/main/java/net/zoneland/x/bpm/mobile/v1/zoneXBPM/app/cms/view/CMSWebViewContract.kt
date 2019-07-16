package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.cms.view

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.AttachmentItemVO
import java.io.File


object CMSWebViewContract {
    interface View : BaseView {
        fun finishLoading()
        fun uploadAttachmentSuccess(attachmentId:String, site:String)
        fun replaceAttachmentSuccess(attachmentId:String, site:String)
        fun downloadAttachmentSuccess(file: File)
        fun downloadAttachmentFail(message: String)
        fun upload2FileStorageFail(message: String)
        fun upload2FileStorageSuccess(id: String)
    }

    interface Presenter : BasePresenter<View> {
        fun uploadAttachment(attachmentFilePath: String, site: String, docId: String)
        fun replaceAttachment(attachmentFilePath: String, site: String, attachmentId: String, docId: String)
        fun downloadAttachment(attachmentId: String, filePath: String)
        fun upload2FileStorage(filePath: String, referenceType: String , reference: String , scale: Int = 500)
    }
}
