package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.im

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.im.IMConversationInfo
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.im.IMMessage

object O2ChatContract  {

    interface View: BaseView {
        fun backPageMessages(list: List<IMMessage>)
        fun sendMessageSuccess(id: String)
        fun sendFail(id: String)
        fun conversationInfo(info: IMConversationInfo)
        fun conversationGetFail()
    }
    interface Presenter: BasePresenter<View> {
        fun sendTextMessage(msg: IMMessage)
        fun getMessage(page: Int, conversationId: String)
        fun readConversation(conversationId: String)
        fun getConversation(id: String)
    }
}