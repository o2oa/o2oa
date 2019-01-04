package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.process

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.identity.ProcessWOIdentityJson


object StartProcessStepTwoContract {
    interface View : BaseView {
        fun loadCurrentPersonIdentity(list:List<ProcessWOIdentityJson>)
        fun loadCurrentPersonIdentityFail()
        fun startProcessSuccess(workId:String)
        fun startProcessFail(message:String)

    }

    interface Presenter : BasePresenter<View> {
        fun loadCurrentPersonIdentityWithProcess(processId: String)
        fun startProcess(title:String, identity:String, processId:String)
    }
}
