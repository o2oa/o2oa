package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.calendar

/**
 * Created by fancyLou on 14/06/2018.
 * Copyright © 2018 O2. All rights reserved.
 */


data class CalendarEventResponseData(
        var inOneDayEvents: ArrayList<CalendarEventInDayData> = ArrayList(),
        var wholeDayEvents: ArrayList<CalendarEventInfoData> = ArrayList()
)