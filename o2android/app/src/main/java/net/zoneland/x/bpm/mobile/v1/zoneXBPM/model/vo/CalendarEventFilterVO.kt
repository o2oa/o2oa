package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo

import java.io.Serializable
import java.util.*

/**
 * Created by fancyLou on 27/06/2018.
 * Copyright © 2018 O2. All rights reserved.
 */

data class CalendarEventFilterVO(
        var start: Calendar? = null,
        var end: Calendar? = null,
        var calendarIds: List<String>? = null
): Serializable