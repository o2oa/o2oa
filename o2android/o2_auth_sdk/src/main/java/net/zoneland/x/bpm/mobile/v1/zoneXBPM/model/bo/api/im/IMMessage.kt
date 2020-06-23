package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.im

import android.text.TextUtils
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager


data class IMMessage(
        var id: String = "",
        var conversationId: String = "",
        var body: String = "",
        var createPerson: String = "",
        var createTime: String = "",

        //消息发送状态 0正常 1发送中 2发送失败要重试
        var sendStatus: Int = 0
) {
    fun messageBody(): IMMessageBody? {
        if (TextUtils.isEmpty(body)) {
            return null
        }
        return O2SDKManager.instance().gson.fromJson(body, IMMessageBody::class.java)
    }
}

enum class MessageType(val key:String) {
    text("text"),
    emoji("emoji"),
    image("image"),
    audio("audio"),
    location("location")
}

enum class MessageBody(val body:String) {
    image("[图片]"),
    audio("[语音]"),
    location("[位置]")
}