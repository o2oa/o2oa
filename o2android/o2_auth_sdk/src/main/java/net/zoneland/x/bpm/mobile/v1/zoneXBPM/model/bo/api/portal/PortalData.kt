package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.portal

/**
 * 门户对象
 * Created by fancyLou on 2018/3/16.
 * Copyright © 2018 O2. All rights reserved.
 */

data class PortalData(
        var createTime:String = "",
        var updateTime:String = "",
        var id:String = "",
        var name:String = "",
        var alias:String = "",
        var description:String = "",
        var portalCategory:String = "",
        var firstPage:String = "",
        var creatorPerson:String = "",
        var lastUpdateTime:String = "",
        var lastUpdatePerson:String = "",
        var enable : Boolean = true,
        var mobileClient: Boolean = true
)