package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.attendance

/**
 * Created by fancyLou on 28/05/2018.
 * Copyright © 2018 O2. All rights reserved.
 */


data class AttendanceStatisticGroupHeader (
        var groupType:Int , // 0 迟到 1 早退 2 缺席 3 正常
        var firstDetail: AttendanceDetailInfoJson
)