package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.yunpan

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.CloudDiskItem
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.CooperationItem
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.YunpanItem
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.friendlyFileLength

/**
 * Created by fancy on 2017/3/29.
 */

/**
 * 云盘文件对象
 */
data class FileJson(
        var id: String = "",
        var createTime: String = "",
        var updateTime: String = "",
        var name: String = "",
        var person: String = "",
        var sequence: String = "",
        var fileName: String = "",
        var extension: String = "",
        var contentType: String = "",
        var storageName: String = "",

        //下面3个 v2版本新增
        var fileId: String = "", //分享对象的时候这个代表文件原始id
        var storage: String = "",
        var type: String = "",

        var length: Long = 0,
        var folder: String = "",
        var lastUpdateTime: String = "",
        var lastUpdatePerson: String = "",
        var shareList: List<String> = ArrayList(),
        var editorList: List<String> = ArrayList()
) {
    fun copyToVO() : YunpanItem.FileItem{
        return YunpanItem.FileItem(id, name, updateTime, extension, fileName, length.friendlyFileLength())
    }

    //v2版本
    fun copyToVO2(): CloudDiskItem.FileItem {
        return CloudDiskItem.FileItem(id, name, createTime, updateTime, person, fileName, extension,
                contentType, storageName, fileId, storage, type, length, folder, lastUpdateTime, lastUpdatePerson)
    }
}

/**
 * 云盘文件夹对象
 */
data class FolderJson(var id: String = "",
                      var createTime: String = "",
                      var updateTime: String = "",
                      var name: String = "",
                      var person: String = "",
                      var sequence: String = "",
                      var superior: String = "",
                      var attachmentCount: Int = 0,
                      var size: Int = 0,
                      var folderCount: Int = 0,
                      //v2版本增加
                      var status: String = "",
                      var fileId: String = "" //分享对象的时候这个代表文件原始id
) {

    fun copyToVO() : YunpanItem.FolderItem {
        return YunpanItem.FolderItem(id, name, updateTime)
    }

    //v2版本
    fun copyToVO2(): CloudDiskItem.FolderItem {
        return CloudDiskItem.FolderItem(id, name, createTime, updateTime, person, superior,
                attachmentCount, size, folderCount, status, fileId)
    }
}

/**
 * 云盘接收到的数据对象
 */
data class YunpanJson(
        var attachmentList: List<FileJson> = ArrayList(),
        var folderList: List<FolderJson> = ArrayList()
)

/**
 * 共享和收到的文件夹对象
 */
data class CooperationFolderJson (
        var name: String = "",
        var value: String = "",
        var count: Int = 0
) {
    fun copyToVO() : CooperationItem.FolderItem {
        return CooperationItem.FolderItem(name, value, count)
    }
}

/**
 * 共享和收到的文件对象
 */
data class CooperationFileJson (
        var name: String = "",
        var contentType: String = "",
        var id: String = "",
        var createTime: String = "",
        var updateTime: String = "",
        var sequence: String = "",
        var person: String = "",
        var fileName: String = "",
        var extension: String = "",
        var attachmentType: String = "",
        var typeValue: String = "",
        var length: Long = 0,
        var shareList: List<String> = ArrayList(),
        var editorList: List<String> = ArrayList(),
        var folder: String = "",
        var lastUpdateTime: String = "",
        var lastUpdatePerson: String = ""
) {
    fun copyToVO() : CooperationItem.FileItem {
        return CooperationItem.FileItem(name, contentType, id, createTime, updateTime, sequence, person,
                fileName, extension, attachmentType, typeValue, length, shareList, editorList, folder,
                lastUpdateTime, lastUpdatePerson)
    }
}

data class CloudDiskShareForm (
         var shareType : String = "member", //分享类型 member
         var fileId : String = "", //分享的文档id或者文件夹id
         var shareUserList : List<String> = ArrayList(), //分享给的用户列表
         var shareOrgList : List<String> = ArrayList() //分享给的组织列表
)

data class CloudDiskPageForm (
        var fileType: String
)