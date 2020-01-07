package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.calendar

/**
 * Created by fancyLou on 27/06/2018.
 * Copyright © 2018 O2. All rights reserved.
 */

data class MyCalendarData(
        var myCalendars: List<CalendarInfoData> = ArrayList(),
        var unitCalendars: List<CalendarInfoData> = ArrayList(),
        var followCalendars: List<CalendarInfoData> = ArrayList()
)