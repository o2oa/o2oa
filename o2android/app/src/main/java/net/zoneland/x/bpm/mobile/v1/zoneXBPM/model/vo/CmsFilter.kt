package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo

/**
 * Created by fancyLou on 2019-07-03.
 * Copyright © 2019 O2. All rights reserved.
 */

class CmsFilter(
        var categoryIdList: List<String> = ArrayList(),
        var creatorList:  List<String> = ArrayList(),
        var documentType: String = "全部"
)