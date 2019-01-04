package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo

/**
 * Created by fancy on 2017/4/18.
 */

data class LoginHistoryVO(
        var id: String = "",
        var loginName: String = "",
        var loginPhone: String = "",
        var lastLoginTime: String = "",
        var unitId: String = ""
) {

    override fun toString(): String {
        return loginPhone
    }
}