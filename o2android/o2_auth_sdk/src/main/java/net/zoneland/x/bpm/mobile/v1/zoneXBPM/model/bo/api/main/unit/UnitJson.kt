package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.unit

import android.os.Build.ID
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.NewContactFragmentVO
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.NewContactListVO

/**
 * 新版组织对象
 */
data class UnitJson(
	var subDirectIdentityCount: Int = 0,//直接成员身份数量
	var subDirectUnitCount: Int = 0,//直接下级组织数量
	var id: String = "",
	var createTime: String = "",
	var updateTime: String = "",
	var unique: String = "",
	var name: String = "",
	var distinguishedName: String = "",//识别名.以@U结尾
	var orderNumber: Int = 0,
	var levelName: String = "",
	var shortName: String = "",
	var pinyinInitial: String = "",
	var pinyin: String = "",
	var level: Int = 0,
	var superior: String = "",
	var woSubDirectUnitList: List<UnitJson> = ArrayList(), //下级组织列表 根据组织类型查询的时候需要用到
	var typeList: ArrayList<String> = ArrayList(),
	var inheritedControllerList: ArrayList<String> = ArrayList(),//继承自上级组织的管理人员.
	var controllerList: ArrayList<String> = ArrayList()//组织的管理人员.
){
	fun copyToVO(): NewContactFragmentVO = NewContactFragmentVO.MyDepartment(distinguishedName, name, (subDirectIdentityCount+subDirectUnitCount)>0)

	fun copyToOrgVO(): NewContactListVO = NewContactListVO.Department(id, name,unique,
			distinguishedName, typeList, shortName, level, levelName, subDirectUnitCount, subDirectIdentityCount)
}
