package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.identity

/**
 * Created by fancyLou on 2019-08-08.
 * Copyright © 2019 O2. All rights reserved.
 */

data class UnitDutyIdentityForm(
        var unit: String = "",
        var name: String = "",
        var unitList:List<String> = ArrayList(),
        var nameList:List<String> = ArrayList()

)