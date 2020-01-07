package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.identity

/**
 * Created by fancyLou on 2019-08-08.
 * Copyright © 2019 O2. All rights reserved.
 */

//用于查询身份上级组织 根据level层级决定
data class IdentityLevelForm(
        var identity: String = "",
        var level: Int = 1
)