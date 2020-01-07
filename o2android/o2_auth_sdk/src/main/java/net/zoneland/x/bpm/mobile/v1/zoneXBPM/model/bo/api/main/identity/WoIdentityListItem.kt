package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.identity

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.unit.UnitJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.unit.duty.UnitDutyJson

/**
 * 身份列表对象 个人详细中使用
 */
data class WoIdentityListItem(
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
        var id: String = "",
        var department: String = "",
        var woUnit: UnitJson? = null,
        var woUnitDutyList: List<UnitDutyJson> = ArrayList()
        )
