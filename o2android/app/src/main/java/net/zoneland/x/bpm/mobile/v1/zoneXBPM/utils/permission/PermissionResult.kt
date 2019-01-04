package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.permission

/**
 * 权限判断结果对象
 *
 * @param granted true 全部检查通过 false 至少有一个不通过
 * @param shouldShowRequestPermissionRationale false 至少有一个不询问
 * @param deniedPermissions 不通过的权限列表
 *
 * Created by fancy on 2017/9/12.
 * Copyright © 2017 O2. All rights reserved.
 */

data class PermissionResult(val granted: Boolean,
                            val shouldShowRequestPermissionRationale: Boolean,
                            val deniedPermissions: List<String>)