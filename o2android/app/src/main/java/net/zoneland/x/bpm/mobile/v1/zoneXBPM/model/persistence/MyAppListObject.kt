package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.persistence

import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey

/**
 * Created by 73419 on 2017/9/27 0027.
 */
open class MyAppListObject : RealmObject() {

    @PrimaryKey
    var appId: String? = null
    var appTitle: String? = null
    var unitId: String? = null
    var userId: String? = null
    var sortId: Int? = null
    @Ignore
    var isClick: Boolean = false

}