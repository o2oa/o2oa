package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.webview

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import java.io.File


object TaskCompletedWebViewContract {
    interface View : BaseView {
        fun downloadAttachment(file: File)
        fun retractSuccess()
        fun retractFail()

        fun invalidateArgs()
        fun downloadFail(message:String)
    }

    interface Presenter : BasePresenter<View> {

        fun retractWork(workId: String)
        fun downloadAttachment(attachmentId: String, workId: String)
        fun downloadWorkCompletedAttachment(attachmentId: String, workId: String)
    }
}
