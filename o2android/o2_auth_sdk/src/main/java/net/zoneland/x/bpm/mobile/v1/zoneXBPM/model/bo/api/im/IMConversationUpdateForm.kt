package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.im


data class IMConversationUpdateForm (
        var id: String? = null,
        var personList: ArrayList<String> = ArrayList(),
        var title: String = "",
        var adminPerson: String? = null,
        var note: String? = null
)