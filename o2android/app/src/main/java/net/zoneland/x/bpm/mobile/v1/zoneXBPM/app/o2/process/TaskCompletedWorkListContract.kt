package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.process

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.TaskCompleteInfoDataWithControl

/**
 * Created by fancyLou on 02/05/2018.
 * Copyright © 2018 O2. All rights reserved.
 */


object TaskCompletedWorkListContract{
    interface View: BaseView {
        fun loadWorkCompletedInfo(info: TaskCompleteInfoDataWithControl)
        fun loadWorkCompletedInfoFail()

    }

    interface Presenter: BasePresenter<View> {
        fun loadTaskCompleteInfo(id: String)
    }
}