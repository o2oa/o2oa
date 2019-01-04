package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.main

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.cms.CMSCategoryInfoJson

/**
 * Created by fancyLou on 21/03/2018.
 * Copyright © 2018 O2. All rights reserved.
 */

object IndexPortalContract {
    interface View:BaseView {
        fun loadCmsCategoryListByAppId(categoryList: List<CMSCategoryInfoJson>)
    }
    interface Presenter:BasePresenter<View> {
        fun loadCmsCategoryListByAppId(appId: String)
    }
}