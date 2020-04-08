package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.calendar

/**
 * Created by fancyLou on 11/07/2018.
 * Copyright © 2018 O2. All rights reserved.
 */

data class CalendarPostData (
        var id: String = "",
        var name: String = "",
        var type: String = "",
        var color: String = "",
        var isPublic: Boolean = false,
        var followed: Boolean = false,
        var description: String = "",
        var status: String = "",
        var createor: String = "",
        //所属组织、人员
        var target: String = "",
        //管理者
        var manageablePersonList: List<String> = ArrayList(),
        //可见范围
        var viewablePersonList: List<String> = ArrayList(),
        var viewableUnitList: List<String> = ArrayList(),
        var viewableGroupList: List<String> = ArrayList(),
        //可新建日程的范围
        var publishablePersonList: List<String> = ArrayList(),
        var publishableUnitList: List<String> = ArrayList(),
        var publishableGroupList: List<String> = ArrayList()
)