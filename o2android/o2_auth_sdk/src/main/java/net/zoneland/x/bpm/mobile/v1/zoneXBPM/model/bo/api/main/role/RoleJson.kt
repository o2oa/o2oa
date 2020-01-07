package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.role

/**
 * 新版角色对象
 */
data class RoleJson(
	var pinyin: String = "",
	var personList: List<String> = ArrayList(),
	var createTime: String = "",
	var unique: String = "",
	var name: String = "",
	var distinguishedName: String = "",
	var updateTime: String = "",
	var groupList: List<String> = ArrayList(),
	var id: String = "",
	var pinyinInitial: String = ""
)
