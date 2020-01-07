package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2

/**
 * Created by fancyLou on 17/05/2018.
 * Copyright © 2018 O2. All rights reserved.
 */


data class TaskNeuralResponseData(
        var routeName: String = "",
        var workLogList: List<WorkLog> = ArrayList()
)