package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.portal.PortalData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.AppItemOnlineVo

/**
 * Created by fancyLou on 16/04/2018.
 * Copyright © 2018 O2. All rights reserved.
 */


data class CustomStyleData(
        var indexType: String = "default",
//        var indexId: String = "",
        var indexPortal: String = "",
        var portalList: List<PortalData> = ArrayList(),
        var nativeAppList: List<AppItemOnlineVo> = ArrayList(),
        var images: ArrayList<ImageValue> = ArrayList()
) {

    data class ImageValue(var name: String = "",
                          var value: String = "")
}