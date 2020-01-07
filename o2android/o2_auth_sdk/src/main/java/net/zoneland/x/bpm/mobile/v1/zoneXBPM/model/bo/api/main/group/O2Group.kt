package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.group

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.NewContactListVO

/**
 * Created by fancyLou on 2018/1/12.
 * Copyright © 2018 O2. All rights reserved.
 */
data class O2Group(var id:String = "",
                   var pinyin:String = "",
                   var pinyinInitial:String = "",
                   var name:String = "",
                   var description:String = "",
                   var unique:String = "",
                   var distinguishedName:String = "",
                   var personList:List<String> = ArrayList(),
                   var groupList:List<String> = ArrayList()) {

    fun copy2NewContactListVO(): NewContactListVO.Group = NewContactListVO.Group(id, name, distinguishedName, unique)
}