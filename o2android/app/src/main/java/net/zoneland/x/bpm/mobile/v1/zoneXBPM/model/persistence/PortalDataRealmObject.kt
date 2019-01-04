package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.persistence

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by fancyLou on 17/04/2018.
 * Copyright © 2018 O2. All rights reserved.
 */
open class PortalDataRealmObject(
        @PrimaryKey
        var id: String? = "",
        var name: String? = "",
        var alias: String? = "",
        var description: String? = "",
        var portalCategory: String? = "",
        var firstPage: String? = "",
        var creatorPerson: String? = "",
        var lastUpdateTime: String? = "",
        var lastUpdatePerson: String? = "",
        var createTime: String? = "",
        var updateTime: String? = "",
        var enable: Boolean? = true
) : RealmObject()