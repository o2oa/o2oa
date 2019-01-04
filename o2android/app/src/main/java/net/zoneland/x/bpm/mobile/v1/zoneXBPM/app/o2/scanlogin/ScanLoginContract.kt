package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.scanlogin

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView


object ScanLoginContract {
    interface View : BaseView {
        fun confirmSuccess()
        fun confirmFail()
    }

    interface Presenter : BasePresenter<View> {
        fun confirmWebLogin(meta:String)
    }
}
