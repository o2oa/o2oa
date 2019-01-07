package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.main

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView

/**
 * Created by fancy on 2017/6/9.
 * Copyright © 2017 O2. All rights reserved.
 */

object SettingsContract {
    interface View: BaseView {
        fun logoutSuccess()
        fun logoutFail()
        fun cleanOver()
    }
    interface Presenter:BasePresenter<View>{
        fun logout()

        fun cleanApp()
    }
}