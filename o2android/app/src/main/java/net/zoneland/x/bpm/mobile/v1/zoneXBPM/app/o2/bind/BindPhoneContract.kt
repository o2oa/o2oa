package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.bind

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView

/**
 * Created by fancyLou on 17/04/2018.
 * Copyright © 2018 O2. All rights reserved.
 */

object BindPhoneContract {
    interface View: BaseView {
        fun customStyle(isNeedUpdate: Boolean)
    }
    interface Presenter: BasePresenter<View> {

        fun checkCustomStyle()
    }
}