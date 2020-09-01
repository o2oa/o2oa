package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.person

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.identity.WoIdentityListItem
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.role.RoleJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.NewContactListVO

/**
 * 新版个人详细信息
 */
data class PersonJson(
        var createTime: String = "",
        var updateTime: String = "",
        var unique: String = "",
        var name: String = "",
        var id: String = "",
        var distinguishedName: String = "",
        var employee: String = "",
        var lastLoginAddress: String = "",
        var lastLoginClient: String = "",
        var lastLoginTime: String = "",
        var orderNumber: Int = 0,
        var mail: String = "",
        var signature: String = "",
        var mobile: String = "",
        var officePhone: String = "",
        var pinyinInitial: String = "",
        var pinyin: String = "",
        var qq: String = "",
        var weixin: String = "",
        var genderType: String = "",
        var changePasswordTime: String = "",
        var superior: String = "",//汇报对象.
        var controllerList: List<String> = ArrayList(),//个人管理者.默认为创建者。
        var woIdentityList: List<WoIdentityListItem> = ArrayList(),
        var woRoleList: List<RoleJson> = ArrayList(),
        var woPersonAttributeList: List<PersonAttributeJson> = ArrayList()
) {
    fun copy2NewContactListVO(): NewContactListVO.Person = NewContactListVO.Person(
            id, name, unique, distinguishedName, genderType, employee, mail, weixin, qq, mobile, ""
    )
}
