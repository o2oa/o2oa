package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.calendar

import java.io.Serializable

/**
 * Created by fancyLou on 2018/11/22.
 * Copyright © 2018 O2. All rights reserved.
 */
/**
 * 日历对象
 * Created by fancyLou on 14/06/2018.
 * Copyright © 2018 O2. All rights reserved.
 */
data class CalendarInfoData(
        var id: String = "",
        var name: String = "",
        var type: String = "",
        var color: String = "",
        var manageable: Boolean = false
): Serializable
