package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo

/**
 * 论坛内容中的图片对象
 * Created by fancy on 2017/3/28.
 */
data class BBSUploadImageBO(var fileId: String = "", //上传图片返回的id
                            var width: Int = 0,//图片实际宽度
                            var height: Int = 0,//图片实际高度
                            var showWidth: Int = 0,//图片展现宽度
                            var showHeight: Int = 0//图片展现高度
)