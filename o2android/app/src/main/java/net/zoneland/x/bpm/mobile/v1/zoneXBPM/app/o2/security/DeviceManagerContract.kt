package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.security

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.CollectDeviceData

/**
 * Created by fancyLou on 2019-05-07.
 * Copyright © 2019 O2. All rights reserved.
 */


object DeviceManagerContract {
    interface View : BaseView {
        fun list(list: List<CollectDeviceData>)
        fun unbindBack(flag: Boolean, message: String)
    }

    interface Presenter : BasePresenter<View> {
        fun listDevice()
        fun unbind(unbindToken: String)
    }
}