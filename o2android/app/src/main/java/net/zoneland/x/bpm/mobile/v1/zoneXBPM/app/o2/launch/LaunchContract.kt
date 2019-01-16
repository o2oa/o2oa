package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.launch

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.AuthenticationInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.CollectUnitData

/**
 * Created by fancy on 2017/6/6.
 */

object LaunchContract {
    interface View : BaseView {

    }

    interface Presenter: BasePresenter<View> {

        fun downloadConfig()
    }
}