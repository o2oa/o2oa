package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.cms

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.AttachmentItemVO
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.ToDoFragmentListViewItemVO
import java.io.Serializable

/**
 * 内容管理接口进出data对象
 * Created by fancy on 2017/3/27.
 */


/**
 * 内容管理附件对象
 */
data class CMSDocumentAttachmentJson(var id: String = "",
                                     var createTime: String = "",
                                     var updateTime: String = "",
                                     var sequence: String = "",
                                     var lastUpdateTime: String = "",
                                     var storage: String = "",
                                     var name: String = "", //文件原名称
                                     var fileName: String = "", //后台存储的文件名
                                     var appId: String = "",
                                     var categoryId: String = "",
                                     var documentId: String = "",
                                     var fileType: String = "",
                                     var fileHost: String = "",
                                     var filePath: String = "",
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
 * 内容管理分类对象
 */
data class CMSCategoryInfoJson(var id: String = "",
                               var createTime: String = "",
                               var updateTime: String = "",
                               var sequence: String = "",
                               var categoryName: String = "",
                               var appId: String = "",
                               var appName: String = "",
                               var categoryAlias: String = "",
                               var formId: String = "",
                               var formName: String = "",
                               var readFormId: String = "",
                               var readFormName: String = "",
                               var categorySeq: String = "",
                               var description: String = "",
                               var creatorPerson: String = "",
                               var creatorIdentity: String = "",
                               var creatorDepartment: String = "",
                               var creatorCompany: String = "",
                               var workflowType: String = "",
                                var workflowAppId: String = "",
                               var workflowAppName: String = "",
                                var workflowName: String = "",
                               //流程id
                               var workflowFlag: String = "") : Serializable

/**
 * 栏目对象
 */
data class CMSApplicationInfoJson(var id: String = "",
                                  var createTime: String = "",
                                  var updateTime: String = "",
                                  var sequence: String = "",
                                  var appName: String = "",
                                  var appAlias: String = "",
                                  var appInfoSeq: String = "",
                                  var description: String = "",
                                  var appIcon: String = "",
                                  var creatorPerson: String = "",
                                  var creatorIdentity: String = "",
                                  var creatorDepartment: String = "",
                                  var creatorCompany: String = "",
                                  var config: String = "", //配置参数用的 json字符串
                                  var categoryList: List<String> = ArrayList(),
                                  var wrapOutCategoryList: List<CMSCategoryInfoJson> = ArrayList()) : Serializable

/**
 * 栏目配置属性
 */
data class CMSAPPConfig(
        var ignoreTitle: Boolean = false,
        var latest: Boolean = true
): Serializable

/**
 * 文章对象
 */
data class CMSDocumentInfoJson(var id: String = "",
                               var createTime: String = "",
                               var updateTime: String = "",
                               var sequence: String = "",
                               var title: String = "",
                               var appId: String = "",
                               var categoryId: String = "",
                               var categoryName: String = "",
                               var categoryAlias: String = "",
                               var form: String = "",
                               var formName: String = "",
                               var creatorPerson: String = "",
                               var creatorIdentity: String = "",
                               var creatorDepartment: String = "",
                               var creatorCompany: String = "",
                               var docStatus: String = "",
                               var publishTime: String = "",
                               var isNewDocument: Boolean = false,
                               var attachmentList: List<String> = ArrayList()) {
    /**
     *
     */
    fun copyToTodoListItem(): ToDoFragmentListViewItemVO {
        val time = if (this.publishTime.length>9) {this.publishTime.substring(0, 10)}else{ this.publishTime}
        return ToDoFragmentListViewItemVO(this.id,
                O2.BUSINESS_TYPE_MESSAGE_CENTER,
                this.title,
                "【" + this.categoryName + "】",
                time)
    }
}