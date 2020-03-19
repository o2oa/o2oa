package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.clouddrive.v2.picker

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.CloudDiskItem


object CloudDiskFolderPickerContract {
    interface View : BaseView {
        fun itemList(list: List<CloudDiskItem>)
        fun error(error: String)
    }
    interface Presenter: BasePresenter<View> {
        fun getItemList(parentId: String)
    }
}