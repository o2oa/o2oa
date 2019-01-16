package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.main

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.NewContactFragmentVO


object NewContactContract {
    interface View : BaseView {
        fun loadContactFail()
        fun loadContact(list:List<NewContactFragmentVO>)
    }

    interface Presenter : BasePresenter<View> {
        fun loadNewContact()
    }
}
