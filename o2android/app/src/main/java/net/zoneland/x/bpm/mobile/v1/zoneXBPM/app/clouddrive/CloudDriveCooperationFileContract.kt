package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.clouddrive

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.CooperationItem


object CloudDriveCooperationFileContract {
    interface View : BaseView {
        fun setFileList(files: List<CooperationItem.FileItem>)
        fun onException(message: String)
        fun setFolderList(folders: List<CooperationItem.FolderItem>)
    }

    interface Presenter : BasePresenter<View> {
        fun loadShareFileList(folderName: String)
        fun loadShareFolderList()
        fun  loadReceiveFileList(folderName: String)
        fun loadReceiveFolderList()
    }
}
