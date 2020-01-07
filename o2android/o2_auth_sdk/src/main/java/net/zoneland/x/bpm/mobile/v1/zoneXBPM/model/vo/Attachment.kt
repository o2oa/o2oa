package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo

/**
 * 附件相关的前端数据对象类
 * Created by fancy on 2017/3/27.
 */


/**
 * 附件ITEM对象
 * cms和bbs附件列表使用
 */
data class AttachmentItemVO(var id:String="",
                            var createTime:String="",
                            var updateTime:String="",
                            var lastUpdateTime:String="",
                            var storage:String="",
                            var name:String="",
                            var fileName:String="",
                            var extension:String="",
                            var length:Long= 0L)

/**
 * webView 页面使用的论坛附件对象
 */
data class BBSWebViewAttachmentVO(var canReply:Boolean = false,
                                  var hasAttach: Boolean = false,
                                  var attachList:List<AttachmentItemVO> = ArrayList())

