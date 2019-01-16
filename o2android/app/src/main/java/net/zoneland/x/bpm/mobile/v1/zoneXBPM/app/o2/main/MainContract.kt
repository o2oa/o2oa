package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.main

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView

/**
 * Created by fancy on 2017/6/8.
 */


object MainContract {
    interface View : BaseView{
        fun o2AIEnable(enable: Boolean)
    }

    interface Presenter: BasePresenter<View> {
        fun checkO2AIEnable()
    }
}