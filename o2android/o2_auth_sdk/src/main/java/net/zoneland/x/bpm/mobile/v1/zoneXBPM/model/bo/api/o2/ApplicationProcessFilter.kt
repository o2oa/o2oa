package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2

/**
 * Created by fancyLou on 2020-07-23.
 * Copyright © 2020 O2. All rights reserved.
 */

data class ApplicationProcessFilter(
        var startableTerminal: String = "mobile" //可启动流程终端类型,可选值 client,mobile,all
)