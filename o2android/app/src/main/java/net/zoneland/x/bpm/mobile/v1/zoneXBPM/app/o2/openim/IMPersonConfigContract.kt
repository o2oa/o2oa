package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.openim

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.person.PersonJson

/**
 * Created by fancyLou on 2018/1/31.
 * Copyright © 2018 O2. All rights reserved.
 */


object IMPersonConfigContract {
    interface View: BaseView{
        fun loadPersonInfo(personInfo: PersonJson)
        fun loadPersonInfoFail()
    }
    interface Presenter: BasePresenter<View>{
        fun loadPersonInfo(name: String)
    }
}