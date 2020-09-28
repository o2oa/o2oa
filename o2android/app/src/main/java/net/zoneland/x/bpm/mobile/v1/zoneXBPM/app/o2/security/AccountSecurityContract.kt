package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.security

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView


object AccountSecurityContract {
    interface View : BaseView {

        fun logoutSuccess()
        fun updateMyPasswordFail(message: String)
        fun updateMyPasswordSuccess()
    }

    interface Presenter : BasePresenter<View> {
        fun logout(deviceId:String)
        fun updateMyPassword(old: String, newPwd: String, newPwdConfirm: String)
        /**
         *
         */
        fun getRSAPublicKey()

    }
}
