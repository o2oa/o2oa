package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.ai

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView

/**
 * Created by fancyLou on 15/05/2018.
 * Copyright © 2018 O2. All rights reserved.
 */


object O2AIContract {
    interface View: BaseView{
        fun beginListen()
        /**
         *
         */
        fun speak(message:String, id:String)
        fun finishAI()
    }
    interface Presenter: BasePresenter<View>{

        fun listenFinish(result: String)
        fun listenError()
        fun speakFinish(utteranceId: String?)
        fun speakError(utteranceId: String?)
    }
}