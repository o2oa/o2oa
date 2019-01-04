package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.bbs.main

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView


object BBSMainContract {
    interface View : BaseView {
        fun whetherThereHasAnyCollections(flag: Boolean)
    }

    interface Presenter : BasePresenter<View> {
        fun whetherThereHasCollections()
    }
}
