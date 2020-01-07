package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.person

/**
 * Created by fancy on 2017/7/11.
 * Copyright © 2017 O2. All rights reserved.
 */

data class PersonListLikeForm(
        var key:String="",
        var groupList:List<String> = ArrayList<String>(),
        var roleList:List<String> = ArrayList<String>()
)