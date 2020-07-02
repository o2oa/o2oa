package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.im.fm

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.InstantMessage
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.im.IMConversationInfo

object O2IMConversationContract {
    interface View: BaseView {
        fun myConversationList(list: List<IMConversationInfo>)
        fun myInstantMessageList(instantList: List<InstantMessage>)
        fun createConvSuccess(conv: IMConversationInfo)
        fun createConvFail(message: String)
    }

    interface Presenter: BasePresenter<View> {
        fun getMyConversationList()
        fun getMyInstantMessageList()
        fun createConversation(type: String, users: ArrayList<String>)
    }
}