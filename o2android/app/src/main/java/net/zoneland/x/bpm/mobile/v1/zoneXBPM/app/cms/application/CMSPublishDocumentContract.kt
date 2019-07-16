package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.cms.application

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.cms.CMSDocumentInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.identity.ProcessWOIdentityJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.identity.WoIdentityListItem

/**
 * Created by fancyLou on 2019-07-03.
 * Copyright © 2019 O2. All rights reserved.
 */

object CMSPublishDocumentContract {
    interface View : BaseView {
        fun currentPersonIdentities(list: List<WoIdentityListItem>)
        fun newDocumentId(id: String)
        fun newDocumentFail()
    }

    interface Presenter : BasePresenter<View> {
        fun findCurrentPersonIdentity()
        fun newDocument(doc: CMSDocumentInfoJson)
    }
}