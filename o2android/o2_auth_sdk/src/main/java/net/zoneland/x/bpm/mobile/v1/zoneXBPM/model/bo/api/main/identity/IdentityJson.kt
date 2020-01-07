package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.identity

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.NewContactFragmentVO
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.NewContactListVO

/**
 * 新版身份信息
 */
data class IdentityJson(
	var unitName: String = "",
	var unitLevel: Int = 0,
	var description: String = "",
	var distinguishedName: String = "",
	var unitLevelName: String = "",
	var updateTime: String = "",
	var pinyinInitial: String = "",
	var unit: String = "",
	var pinyin: String = "",
	var createTime: String = "",
	var person: String = "",
	var unique: String = "",
	var name: String = "",
	var id: String = ""
){
    fun copyToVO(): NewContactFragmentVO = NewContactFragmentVO.MyDepartment(unit, unitName)

	fun copyToOrgVO(): NewContactListVO =  NewContactListVO.Identity(
			id, name, person, distinguishedName, unit, unitName, unique, unitLevel, unitLevelName
	)
}
