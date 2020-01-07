package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2

/**
 * Created by fancyLou on 03/05/2018.
 * Copyright © 2018 O2. All rights reserved.
 */


data class WorkControlData(
        var allowVisit: Boolean? = true,
        var allowProcessing: Boolean? = false,
        var allowReadProcessing: Boolean? = false,
        var allowSave: Boolean? = false,
        var allowReset: Boolean? = false,
        var allowRetract: Boolean? = false,
        var allowReroute: Boolean? = false,
        var allowDelete: Boolean? = false
)