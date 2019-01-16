package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.bbs.reply

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.bbs.ReplyFormJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.bbs.SubjectReplyInfoJson


object BBSReplyContract {
    interface View : BaseView {
        fun uploadFail(tag: String)
        fun uploadSuccess(fileId: String, tag: String)
        fun getReplyParentFail()
        fun getReplyParentSuccess(info: SubjectReplyInfoJson)
        fun publishReplyFail()
        fun publishReplySuccess(id: String)
    }

    interface Presenter : BasePresenter<View> {
        fun getReplyParentInfo(parentId: String)
        fun postReply(form: ReplyFormJson)
        fun uploadImage(filePath: String, newReplyId: String)
    }
}
