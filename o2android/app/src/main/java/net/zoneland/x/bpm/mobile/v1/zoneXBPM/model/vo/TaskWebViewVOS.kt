package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo

/**
 * Created by fancyLou on 2019/4/19.
 * Copyright © 2019 O2. All rights reserved.
 */


/**
 * 网页上图片控件 传入的对象
 * {"mwfId":"imageclipper","callback":"o2.imageClipperCallback","referencetype":"processPlatformJob","reference":"c3d73214-ade8-47fe-9462-68493342ddfa"}
 */
data class O2UploadImageData(
        var mwfId: String = "",//控件id
        var callback: String = "",//回调函数
        var referencetype: String = "",//上传文件服务器的业务类型名称
        var reference: String = "",//关联业务id
        var fileId: String = ""//上传返回的文件id

)
