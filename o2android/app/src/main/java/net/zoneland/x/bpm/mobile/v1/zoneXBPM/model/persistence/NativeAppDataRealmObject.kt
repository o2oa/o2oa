package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.persistence

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by fancyLou on 17/04/2018.
 * Copyright © 2018 O2. All rights reserved.
 */

open class NativeAppDataRealmObject(
        @PrimaryKey
        var id:Int?  = 0,
        var key:String? = "",
        var name:String? = "",
        var enable:Boolean? = true
): RealmObject()