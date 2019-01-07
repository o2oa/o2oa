package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.security

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView


object AccountSecurityContract {
    interface View : BaseView {

        fun logoutSuccess()
    }

    interface Presenter : BasePresenter<View> {
        fun logout(deviceId:String)
    }
}
