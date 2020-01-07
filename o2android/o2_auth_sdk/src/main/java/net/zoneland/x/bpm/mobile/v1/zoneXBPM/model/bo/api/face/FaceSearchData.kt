package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.face

/**
 * Created by fancyLou on 2018/10/11.
 * Copyright © 2018 O2. All rights reserved.
 */

data class FaceSearchData(
        var time_used: Int = 0,
        var image_id: String = "",
        var request_id: String = "",
        var results: ArrayList<FaceResult> = ArrayList()
)