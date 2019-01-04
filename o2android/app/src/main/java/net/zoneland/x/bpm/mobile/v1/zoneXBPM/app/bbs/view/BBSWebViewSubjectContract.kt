package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.bbs.view

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.bbs.ReplyFormJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.bbs.SubjectReplyInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.BBSWebViewAttachmentVO


object BBSWebViewSubjectContract {
    interface View : BaseView {
        fun loadReplyPermissionAndAttachList(attachment: BBSWebViewAttachmentVO)
        fun publishReplyFail()
        fun publishReplySuccess(id: String)
        fun uploadFail(tag: String)
        fun uploadSuccess(fileId: String, tag: String)
        fun getReplyParentFail()
        fun getReplyParentSuccess(info: SubjectReplyInfoJson)
    }

    interface Presenter : BasePresenter<View> {
        fun loadReplyPermissionAndAttachList(subjectId:String)
        fun postReply(form: ReplyFormJson)
        fun uploadImage(filePath: String, newReplyId: String)
        fun getReplyParentInfo(parentId: String)
    }
}
