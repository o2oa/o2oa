package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.bbs

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.AttachmentItemVO

/**
 * BBS 接口进出的data对象
 * Created by fancy on 2017/3/27.
 */


/**
 * 论坛附件对象
 */
data class BBSSubjectAttachmentJson(var id: String = "",
                                    var createTime: String = "",
                                    var updateTime: String = "",
                                    var sequence: String = "",
                                    var lastUpdateTime: String = "",
                                    var storage: String = "",
                                    var forumId: String = "",
                                    var forumName: String = "",
                                    var sectionId: String = "",
                                    var sectionName: String = "",
                                    var mainSectionId: String = "",
                                    var mainSectionName: String = "",
                                    var subjectId: String = "",
                                    var title: String = "",
                                    var name: String = "",
                                    var fileName: String = "",
                                    var fileHost: String = "",
                                    var filePath: String = "",
                                    var storageName: String = "",
                                    var description: String = "",
                                    var creatorUid: String = "",
                                    var extension: String = "",
                                    var length: Long = 0L) {


    /**
     * 将附件对象copy到VO对象中
     */
    fun copyToVO(): AttachmentItemVO {
        return AttachmentItemVO(id, createTime, updateTime, lastUpdateTime, storage, name, fileName, extension, length)
    }

}

/**
 * 板块对象
 */
data class SectionInfoJson(var id: String = "",
                           var createTime: String = "",
                           var updateTime: String = "",
                           var sequence: String = "",
                           var sectionName: String = "",
                           var forumId: String = "",
                           var forumName: String = "",
                           var mainSectionId: String = "",
                           var mainSectionName: String = "",
                           var sectionLevel: String = "",
                           var sectionDescription: String = "",
                           var sectionNotice: String = "",
                           var subjectType: String ="",
                           var typeCategory: String ="",
                           var icon: String = "",
                           var subjectTotal: Int = 0,
                           var replyTotal: Int = 0,
                           var subjectTotalToday: Int = 0,
                           var replyTotalToday: Int = 0,
                           var sectionStatus: String = "",
                           var orderNumber: Int = 0,
                           var isCollection: Boolean = false)

/**
 * 分区
 */
data class ForumInfoJson(var id: String = "",
                         var createTime: String = "",
                         var updateTime: String = "",
                         var sequence: String = "",
                         var forumName: String = "",
                         var forumManagerName: String = "",
                         var forumNotice: String = "",
                         var sectionTotal: Int = 0,
                         var subjectTotal: Int = 0,
                         var replyTotal: Int = 0,
                         var subjectTotalToday: Int = 0,
                         var replyTotalToday: Int = 0,
                         var forumStatus: String = "",
                         var orderNumber: Int = 0,
                         var sectionInfoList: List<SectionInfoJson> = ArrayList())

/**
 * 是否有发帖权限
 */
data class SubjectPublishPermissionCheckJson(var checkResult: Boolean = false)

/**
 * 是否能回帖
 */
data class SubjectReplyPermissionCheckJson(var replyPublishAble: Boolean = false)

/**
 * 回帖表单对象
 */
data class ReplyFormJson(var id: String = "",
                         var content: String = "",
                         var subjectId: String = "",
                         var parentId: String = "",
                         var replyMachineName: String = "",
                         var replySystemName: String = O2.DEVICE_TYPE)

/**
 * 发帖表单对象
 */
data class SubjectPublishFormJson(var id: String = "",
                                  var type: String = "",
                                  var typeCategory: String = "",
                                  var title: String = "",
                                  var summary: String = "",
                                  var content: String = "",
                                  var sectionId: String = "",
                                  var subjectMachineName: String = "",
                                  var subjectSystemName: String = O2.DEVICE_TYPE,
                                  var attachmentList: List<String> = ArrayList()
)

/**
 * 帖子信息对象
 */
data class SubjectInfoJson(var id: String = "",
                           var createTime: String = "",
                           var updateTime: String = "",
                           var sequence: String = "",
                           var forumId: String = "",
                           var forumName: String = "",
                           var sectionId: String = "",
                           var sectionName: String = "",
                           var mainSectionId: String = "",
                           var mainSectionName: String = "",
                           var title: String = "",
                           var creatorName: String = "",
                           var creatorNameShort: String = "",
                           var type: String = "",
                           var summary: String = "",
                           var content: String = "",
                           var latestReplyTime: String = "",
                           var latestReplyUser: String = "",
                           var latestReplyId: String = "",
                           var replyTotal: Int = 0,
                           var viewTotal: Int = 0,
                           var hot: Int = 0,
                           var stopReply: Boolean = false,
                           var recommendToBBSIndex: Boolean = false,
                           var bBSIndexSetterName: String = "",
                           var recommendToForumIndex: Boolean = false,
                           var forumIndexSetterName: String = "",
                           var topToSection: Boolean = false,
                           var topToMainSection: Boolean = false,
                           var topToForum: Boolean = false,
                           var topToBBS: Boolean = false,
                           var isTopSubject: Boolean = false,
                           var isCreamSubject: Boolean = false,
                           var attachmentList: List<String> = ArrayList(),
                           var subjectAttachmentList: List<BBSSubjectAttachmentJson> = ArrayList()
)


/**
 * 回帖信息对象
 */
data class SubjectReplyInfoJson(var id: String = "",
                                var createTime: String = "",
                                var updateTime: String = "",
                                var sequence: String = "",
                                var forumId: String = "",
                                var forumName: String = "",
                                var sectionId: String = "",
                                var sectionName: String = "",
                                var mainSectionId: String = "",
                                var mainSectionName: String = "",
                                var subjectId: String = "",
                                var title: String = "",
                                var parentId: String = "",
                                var content: String = "",
                                var creatorName: String = "",
                                var creatorNameShort: String = "",
                                var orderNumber: Int = 0
)





