package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.cms

/**
 * Created by fancyLou on 2020-09-07.
 * Copyright © 2020 O2. All rights reserved.
 */

data class CMSDocumentFilter(
        var statusList: ArrayList<String> = ArrayList(),
        var categoryIdList: ArrayList<String> = ArrayList(),
        var orderField: String = ""
)