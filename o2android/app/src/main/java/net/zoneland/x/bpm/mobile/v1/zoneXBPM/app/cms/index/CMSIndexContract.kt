package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.cms.index

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.cms.CMSApplicationInfoJson


object CMSIndexContract {
    interface View : BaseView {
        fun loadApplicationFail()
        fun loadApplicationSuccess(list: List<CMSApplicationInfoJson>)
    }

    interface Presenter : BasePresenter<View> {
        fun findAllApplication()
    }
}
