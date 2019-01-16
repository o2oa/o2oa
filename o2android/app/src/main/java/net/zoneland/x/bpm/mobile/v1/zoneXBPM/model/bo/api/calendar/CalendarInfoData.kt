package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.calendar

import com.bigkoo.pickerview.model.IPickerViewData
import java.io.Serializable

/**
 * 日历对象
 * Created by fancyLou on 14/06/2018.
 * Copyright © 2018 O2. All rights reserved.
 */
data class CalendarInfoPickViewData(
        var id: String = "",
        var name: String = "",
        var type: String = "",
        var color: String = "",
        var manageable: Boolean = false
): Serializable, IPickerViewData
{
    override fun getPickerViewText(): String = this.name


    fun fromCalendar(info: CalendarInfoData) {
        this.id = info.id
        this.name = info.name
        this.type = info.type
        this.color = info.color
        this.manageable = info.manageable
    }
}