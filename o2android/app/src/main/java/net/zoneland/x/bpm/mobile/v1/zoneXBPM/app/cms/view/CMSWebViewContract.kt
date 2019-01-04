package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.cms.view

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.AttachmentItemVO


object CMSWebViewContract {
    interface View : BaseView {
        fun loadAttachList(list:List<AttachmentItemVO>)
    }

    interface Presenter : BasePresenter<View> {
        fun loadAttachList(docId: String)
    }
}
