package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main

/**
 * 当前认证用户信息
 * Created by fancy on 2017/3/28.
 */
data class AuthenticationInfoJson(
        var token: String = "",
        var tokenType: String = "",//（anonymous cihper manager user ） cipher代表是服务器之间的连接
        var id: String = "",
        var distinguishedName:String = "",
        var unique: String = "",
        var createTime: String = "",
        var updateTime: String = "",
        var genderType: String = "",
        var pinyin: String = "",
        var pinyinInitial: String = "",
        var name: String = "",
        var icon: String = "",//头像
        var employee: String = "",
        var display: String = "",
        var mail: String = "",
        var qq: String = "",
        var weixin: String = "",
        var mobile: String = "",
        var signature: String = "",
        var superior:String = "",//汇报关系
        var controllerList: List<String> = ArrayList(),
        var deviceList: List<String> = ArrayList(),
        var roleList: List<String> = ArrayList()
)