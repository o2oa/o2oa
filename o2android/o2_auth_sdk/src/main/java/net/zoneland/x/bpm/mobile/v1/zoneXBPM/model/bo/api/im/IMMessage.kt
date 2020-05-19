package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.im

import android.text.TextUtils
import org.json.JSONObject
import org.json.JSONTokener

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
        val json = JSONTokener(body).nextValue()
        if (json is JSONObject) {
            try {
                if (json.has("type")) {
                    val type = json.getString("type")
                    if (MessageType.text.key == type) {
                        val textBody = json.getString("body")
                        return IMMessageBody.Text(textBody)
                    }else if(MessageType.emoji.key == type) {
                        val textBody = json.getString("body")
                        return IMMessageBody.Emoji(textBody)
                    }
                }else {
                    val textBody = json.getString("body")
                    return IMMessageBody.Text(textBody)
                }
            } catch (e: Exception) {
            }

        }
        return null
    }


}

enum class MessageType(val key:String) {
    text("text"),
    emoji("emoji")
}