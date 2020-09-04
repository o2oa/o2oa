package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.im

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView


/**
 * Created by fancyLou on 2020-05-25.
 * Copyright © 2020 O2. All rights reserved.
 */
object O2InstantMessageContract {
    interface View: BaseView {
        fun workIsCompleted(flag: Boolean, workId: String)
    }
    interface Presenter: BasePresenter<View> {
        fun getWorkInfo(workId: String)
    }
}