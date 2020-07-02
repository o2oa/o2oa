package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api

import android.os.Parcel
import android.os.Parcelable


/**
 * Created by fancyLou on 2020-05-25.
 * Copyright © 2020 O2. All rights reserved.
 */

data class InstantMessageData(
        var id: String = "",
        var title: String = "",
        var type: String = "",
        var body: String = "",
        var person: String = "",
        var consumerList: ArrayList<String> = ArrayList(),
        var consumed: Boolean = false,
        var createTime: String = "",
        var updateTime: String = ""
)