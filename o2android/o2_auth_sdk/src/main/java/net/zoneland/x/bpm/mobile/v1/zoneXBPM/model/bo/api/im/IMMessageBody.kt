package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.im



sealed class IMMessageBody(open var  type: String) {

    class Text(var  body: String): IMMessageBody(MessageType.text.key)
    class Emoji(var  body: String): IMMessageBody(MessageType.emoji.key)


}