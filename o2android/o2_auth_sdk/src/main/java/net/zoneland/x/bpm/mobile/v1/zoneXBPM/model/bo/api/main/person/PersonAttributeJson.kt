package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.person

/**
 * 新版个人属性
 */
data class PersonAttributeJson(
	var pinyin: String = "",
	var createTime: String = "",
	var person: String = "",
	var unique: String = "",
	var attributeList: List<String> = ArrayList(),
	var name: String = "",
	var description: String = "",
	var distinguishedName: String = "",
	var updateTime: String = "",
	var id: String = "",
	var pinyinInitial: String = ""
)
