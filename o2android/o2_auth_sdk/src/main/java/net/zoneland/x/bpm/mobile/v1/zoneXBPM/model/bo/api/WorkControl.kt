package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api

/**
 * Created by fancyLou on 2018/8/22.
 * Copyright © 2018 O2. All rights reserved.
 */
class WorkControl(
        var allowVisit:Boolean = false,
        var allowProcessing:Boolean = false,
        var allowReadProcessing:Boolean = false,
        var allowSave:Boolean = false,
        var allowRetract:Boolean = false,
        var allowDelete: Boolean = false
)
