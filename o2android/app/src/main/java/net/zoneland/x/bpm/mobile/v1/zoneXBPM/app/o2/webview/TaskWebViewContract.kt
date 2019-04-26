package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.webview

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.ReadData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.TaskData
import java.io.File


object TaskWebViewContract {
    interface View : BaseView {
        fun finishLoading()
        fun submitSuccess()
        fun saveSuccess()
        fun setReadCompletedSuccess()
        fun uploadAttachmentSuccess(attachmentId:String, site:String)
        fun replaceAttachmentSuccess(attachmentId:String, site:String)
        fun downloadAttachmentSuccess(file:File)
        fun invalidateArgs()
        fun downloadFail(message:String)
        fun retractSuccess()
        fun retractFail()
        fun deleteSuccess()
        fun deleteFail()
        fun upload2FileStorageFail(message: String)
        fun upload2FileStorageSuccess(id: String)
    }

    interface Presenter : BasePresenter<View> {
        fun uploadAttachment(attachmentFilePath: String, site: String, workId: String)
        fun replaceAttachment(attachmentFilePath: String, site: String, attachmentId: String, workId: String)
        fun downloadAttachment(attachmentId: String, workId: String)
        fun save(workId: String, formData: String)
        fun submit(data: TaskData?, workId: String, formData: String?)
        fun delete(workId: String)
        fun setReadComplete(read: ReadData?)
        fun retractWork(workId: String)

        fun upload2FileStorage(filePath: String, referenceType: String , reference: String , scale: Int = 800)
    }
}