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
            val type = json.getString("type")
            if ("text" == type) {
                val textBody = json.getString("body")
                return IMMessageBody.Text(textBody)
            }
        }
        return null
    }


}