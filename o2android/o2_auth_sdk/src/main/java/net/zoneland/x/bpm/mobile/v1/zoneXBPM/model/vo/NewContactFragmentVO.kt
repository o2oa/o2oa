package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo


/**
 * Created by fancy on 2017/7/10.
 * Copyright © 2017 O2. All rights reserved.
 */

sealed class NewContactFragmentVO {

    class GroupHeader(var name: String = "",
                      var resId: Int = -1) : NewContactFragmentVO()

    class MyDepartment(var unit: String = "",
                       var unitName: String = "",
                       var hasChildren: Boolean = true) : NewContactFragmentVO()

    class MyCollect(var personId: String = "",
                    var personName: String = "",
                    var gender: String = "",
                    var mobile: String = "") : NewContactFragmentVO()

}