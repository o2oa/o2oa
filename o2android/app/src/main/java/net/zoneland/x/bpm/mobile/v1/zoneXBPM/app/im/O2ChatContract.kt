package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.im

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.im.IMConversationInfo
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.im.IMMessage
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.im.IMMessageBody

object O2ChatContract  {

    interface View: BaseView {
        fun backPageMessages(list: List<IMMessage>)
        fun sendMessageSuccess(id: String)
        fun sendFail(id: String)
        fun conversationInfo(info: IMConversationInfo)
        fun conversationGetFail()
        fun localFile(filePath: String, msgType: String, position: Int)
        fun downloadFileFail(msg: String)
        fun updateSuccess(info: IMConversationInfo)
        fun updateFail(msg: String)
    }
    interface Presenter: BasePresenter<View> {
        fun sendIMMessage(msg: IMMessage)
        fun getMessage(page: Int, conversationId: String)
        fun readConversation(conversationId: String)
        fun getConversation(id: String)
        fun getFileFromNetOrLocal(position: Int, body: IMMessageBody)
        fun updateConversationTitle(id: String, title: String)
        fun updateConversationPeople(id: String, users: ArrayList<String>)
    }
}