package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo



data class O2AppUpdateBean(
        var versionName: String = "", // app 显示版本号
        var buildNo: String  = "", // app内部版本号 一个数字
        var downloadUrl: String = "", //下载地址
        var content: String = "" //更新内容
)