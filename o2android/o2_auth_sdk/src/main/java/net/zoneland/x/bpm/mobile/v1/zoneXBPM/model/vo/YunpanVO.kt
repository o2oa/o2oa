package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo

/**
 * Created by fancy on 2017/3/29.
 */

/**
 * 云盘对象
 */
sealed class YunpanItem(open var id: String, open var name: String, open var updateTime: String) {
    /**
     * 文件对象
     */
    class FileItem(override var id: String, override var name: String, override var updateTime: String, var extension: String, var fileName: String,var length: String) : YunpanItem(id, name, updateTime)

    /**
     * 文件夹对象
     */
    class FolderItem(override var id: String, override var name: String, override var updateTime: String) : YunpanItem(id, name, updateTime)
}


sealed class CooperationItem(open var name: String) {
    class FileItem(override var name: String = "",
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
           ) : CooperationItem(name)

    class FolderItem(override var name: String = "",var value: String = "",var count: Int = 0) : CooperationItem(name)
}

/**
 * v2 版本 文件对象
 */
sealed class CloudDiskItem(open var id: String, open var name: String) {
    class FileItem(override var id: String, override var name: String,
                   var createTime: String,
                   var updateTime: String,
                   var person: String,
                   var fileName: String,
                   var extension: String,
                   var contentType: String,
                   var storageName: String,
                   var fileId: String, //分享对象的时候这个代表文件原始id
                   var storage: String,
                   var type: String,
                   var length: Long,
                   var folder: String,
                   var lastUpdateTime: String,
                   var lastUpdatePerson: String): CloudDiskItem(id, name)
    class FolderItem(override var id: String, override var name: String,
                     var createTime: String,
                     var updateTime: String,
                     var person: String,
                     var superior: String,
                     var attachmentCount: Int,
                     var size: Int,
                     var folderCount: Int,
                     var status: String,
                     var fileId: String ): CloudDiskItem(id, name)
}