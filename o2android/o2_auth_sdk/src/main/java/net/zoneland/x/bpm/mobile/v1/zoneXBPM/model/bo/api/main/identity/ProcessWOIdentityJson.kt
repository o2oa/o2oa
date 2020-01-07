package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.identity

/**
 * Created by fancy on 2017/7/17.
 * Copyright © 2017 O2. All rights reserved.
 */
data class ProcessWOIdentityJson(
        var name: String = "",
        var unique: String = "",
        var description:String="",
        var distinguishedName: String = "",
        var person: String = "",
        var unit: String = "",
        var unitName: String = "",
        var unitLevel: Int = 0,
        var unitLevelName: String = ""
)