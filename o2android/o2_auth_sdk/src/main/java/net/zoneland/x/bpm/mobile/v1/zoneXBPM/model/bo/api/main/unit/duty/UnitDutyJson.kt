package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.unit.duty

/**
 * 新版身份职务对象
 */
data class UnitDutyJson(
		var unit: String = "",
		var pinyin: String = "",
		var createTime: String = "",
		var identityList: List<String> = ArrayList(),
		var unique: String = "",
		var name: String = "",
		var description: String = "",
		var distinguishedName: String = "",
		var updateTime: String = "",
		var id: String = "",
		var pinyinInitial: String = ""
)
