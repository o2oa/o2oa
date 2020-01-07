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
        var target: String = "",
        var description: String = "",
        var status: String = "",
        var createor: String = ""
)