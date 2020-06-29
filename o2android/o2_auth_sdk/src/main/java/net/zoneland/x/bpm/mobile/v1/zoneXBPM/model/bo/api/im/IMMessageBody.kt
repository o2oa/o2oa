package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.im


class IMMessageBody(
        var type: String?,
        var body: String?,
        var fileId: String? = null, //文件id
        var fileExtension: String? = null, //文件扩展
        var fileTempPath: String? = null, //本地临时文件地址
        var audioDuration: String? = null, // 音频文件时长
        var address: String? = null, //type=location的时候位置信息
        var addressDetail: String? = null,
        var latitude: Double? = null,//type=location的时候位置信息
        var longitude: Double? = null//type=location的时候位置信息
)

