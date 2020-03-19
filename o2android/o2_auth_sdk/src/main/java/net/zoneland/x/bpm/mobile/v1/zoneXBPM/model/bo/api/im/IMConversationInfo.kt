package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.im


data class IMConversationInfo(
        var id: String = "",
        var type: String = "",
        var personList: ArrayList<String> = ArrayList(),
        var title: String = "",
        var adminPerson: String = "",
        var note: String = "",
        var unreadNumber: Int = 0,
        var isTop: Boolean = false,
        var createTime : String? = null,
        var updateTime : String? = null,
        var lastMessage: IMMessage? = null

)