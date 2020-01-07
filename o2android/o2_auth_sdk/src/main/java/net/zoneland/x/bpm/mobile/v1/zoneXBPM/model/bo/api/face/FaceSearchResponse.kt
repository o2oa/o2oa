package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.face

/**
 * Created by fancyLou on 2018/10/11.
 * Copyright © 2018 O2. All rights reserved.
 */
data class FaceSearchResponse(
        var type: String = "",//success ??
        var data: FaceSearchData? = null
)