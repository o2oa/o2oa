package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.cms.application

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.cms.CMSDocumentInfoJson


object CMSCategoryContract {
    interface View : BaseView {
        fun loadFail()
        fun loadSuccess(list: List<CMSDocumentInfoJson>)
    }

    interface Presenter : BasePresenter<View> {
        fun findDocumentByPage(id: String, lastId: String)
    }
}
