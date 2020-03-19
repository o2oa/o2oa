package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.clouddrive.v2.f

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.yunpan.FileJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.yunpan.FolderJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.CloudDiskItem
import java.io.File


object FileFolderListContract {
    interface View: BaseView {
        fun itemList(list: List<CloudDiskItem>)
        fun error(error: String)
        fun createFolderSuccess()
        fun uploadSuccess()
        fun updateSuccess()
        fun deleteSuccess()
        fun shareSuccess()
        fun moveSuccess()
    }
    interface Presenter: BasePresenter<View> {
        fun getItemList(parentId: String)
        fun createFolder(params: HashMap<String, String>)
        fun uploadFile(parentId: String, file: File)
        fun updateFile(file: FileJson)
        fun updateFolder(folder: FolderJson)
        fun deleteBatch(fileIds: List<String>, folderIds: List<String>)
        fun share(ids: List<String>, users: List<String>, orgs: List<String>)
        fun move(files: List<CloudDiskItem.FileItem>, folders: List<CloudDiskItem.FolderItem>, destFolderId: String)
    }
}