package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.permission

/**
 * Created by fancy on 2017/9/11.
 * Copyright © 2017 O2. All rights reserved.
 */


data class Permission(val name:String,
                      val granted: Boolean,
                      val shouldShowRequestPermissionRationale: Boolean)