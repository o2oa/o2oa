package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.custom

import android.os.Handler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView

/**
 * Created by fancyLou on 16/04/2018.
 * Copyright © 2018 O2. All rights reserved.
 */


object CustomStyleFragmentContract {
    interface View : BaseView {

        fun installFinish()

    }

    interface Presenter: BasePresenter<View> {
        fun installCustomStyle(handler: Handler?)
    }
}