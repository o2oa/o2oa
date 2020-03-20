package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.clouddrive.v2.type

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.yunpan.FileJson

object CloudDiskFileTypeContract  {
    interface View : BaseView {
        fun pageItems(items: List<FileJson>)
        fun error(error: String)
    }
    interface Presenter : BasePresenter<View> {
        fun getPageItems(page: Int, type: String)
    }
}