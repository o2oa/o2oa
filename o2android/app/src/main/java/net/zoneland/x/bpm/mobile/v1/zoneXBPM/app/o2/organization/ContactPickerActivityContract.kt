package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.organization

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.person.PersonJson

/**
 * Created by fancyLou on 2019-08-20.
 * Copyright © 2019 O2. All rights reserved.
 */

object ContactPickerActivityContract {
    interface View: BaseView{
        fun setPersonInfo(info: PersonJson, type: String)
    }
    interface Presenter: BasePresenter<View> {
        fun getPerson(dn: String, type: String)
    }
}