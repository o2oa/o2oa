package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.cms.application

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.cms.CMSCategoryInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.cms.CMSDocumentInfoJson


object CMSApplicationContract {
    interface View : BaseView {
        fun canPublishCategories(list: List<CMSCategoryInfoJson>)
        fun documentDraft(list: List<CMSDocumentInfoJson>)
    }

    interface Presenter : BasePresenter<View> {
        fun loadCanPublishCategory(appId: String)
        fun findDocumentDraftWithCategory(categoryId: String)
    }
}
