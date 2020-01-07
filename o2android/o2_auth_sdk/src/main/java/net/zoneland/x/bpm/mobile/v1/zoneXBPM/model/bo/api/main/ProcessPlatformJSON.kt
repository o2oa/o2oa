package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main

/**
 * Created by fancy on 2017/4/7.
 */

/**
 * 附件对象
 */
data class AttachmentInfo(
        var id: String = "",
        var createTime: String = "",
        var updateTime: String = "",
        var name: String = "",
        var extension: String = "",
        var storage: String = "",
        var length: Long = 0,
        var workCreateTime: String = "",
        var application: String = "",
        var process: String = "",
        var job: String = "",
        var person: String = "",
        var lastUpdateTime: String = "",
        var lastUpdatePerson: String = "",
        var activity: String = "",
        var activityName: String = "",
        var activityType: String = "",
        var activityToken: String = "",
        var site: String = ""
)