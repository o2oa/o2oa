package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.openim

import android.widget.TextView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView

/**
 * Created by fancyLou on 2018/2/1.
 * Copyright © 2018 O2. All rights reserved.
 */


object IMTribeInfoContract {
    interface View : BaseView {

    }

    interface Presenter: BasePresenter<View> {
        fun asyncLoadPersonName(tv: TextView, personId:String)
    }
}