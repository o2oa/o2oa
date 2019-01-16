package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.clouddrive

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.enums.AttachmentType
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.enums.FileOperateType
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.YunpanItem
import java.io.File


object CloudDriveMyFileContract {
    interface View : BaseView {
        fun loadFileList(list: List<YunpanItem>)
        fun responseErrorMessage(message: String)
        fun refreshView(message: String)
    }

    interface Presenter : BasePresenter<View> {
        fun uploadFile2Folder(folderId: String?, upFile: File)
        fun uploadFile2Top(upFile: File)
        fun createFolder(params: HashMap<String, String>)
        fun shareOrSendFile(id: String, sendList: ArrayList<String>, type: FileOperateType)
        fun deleteFile(folder: AttachmentType, id: String)
        fun reNameFolder(id: String, content: String)
        fun reNameFile(id: String, content: String)
        fun loadFileList(id: String)
    }
}
