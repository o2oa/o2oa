package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo

import com.bigkoo.pickerview.model.IPickerViewData
import java.io.Serializable

/**
 * Created by fancyLou on 03/07/2018.
 * Copyright © 2018 O2. All rights reserved.
 */


data class CalendarPickerOption(
        var name: String = "",
        var value: String = ""
): Serializable, IPickerViewData {
    override fun getPickerViewText(): String  = name
}