package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.webview

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.ReadCompleteInfoData
import java.io.File


object ReadCompletedWebViewContract {
    interface View : BaseView {
        fun loadReadCompletedInfo(info: ReadCompleteInfoData)
        fun finishLoading()
        fun downloadAttachment(file: File)
        fun invalidateArgs()
        fun downloadFail(message:String)
    }

    interface Presenter : BasePresenter<View> {
        fun loadReadCompletedInfo(id:String)
        fun downloadAttachment(attachmentId: String, workId: String)
        fun downloadWorkCompletedAttachment(attachmentId: String, workId: String)
    }
}
