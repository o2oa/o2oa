package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.webview

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.ReadData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.ReadInfoData
import java.io.File


object ReadWebViewContract {
    interface View : BaseView {
        fun loadReadInfo(info: ReadInfoData)
        fun finishLoading()
        fun setReadCompletedSuccess()
        fun downloadAttachment(file: File)
        fun invalidateArgs()
        fun downloadFail(message:String)
    }

    interface Presenter : BasePresenter<View> {
        fun loadReadInfo(id:String)
        fun setReadComplete(read: ReadData?)
        fun downloadAttachment(attachmentId: String, workId: String)
        fun downloadWorkCompletedAttachment(attachmentId: String, workId: String)
    }
}
