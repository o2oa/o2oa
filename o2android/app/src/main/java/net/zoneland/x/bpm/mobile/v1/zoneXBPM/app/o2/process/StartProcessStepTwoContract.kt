package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.process

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.identity.ProcessWOIdentityJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.ProcessDraftWorkData


object StartProcessStepTwoContract {
    interface View : BaseView {
        fun loadCurrentPersonIdentity(list:List<ProcessWOIdentityJson>)
        fun loadCurrentPersonIdentityFail()
        fun startProcessSuccess(workId:String)
        fun startProcessFail(message:String)
        fun startDraftSuccess(work: ProcessDraftWorkData)
        fun startDraftFail(message:String)

    }

    interface Presenter : BasePresenter<View> {
        fun loadCurrentPersonIdentityWithProcess(processId: String)
        fun startProcess(title:String, identity:String, processId:String)
        fun startDraft(title:String, identity: String, processId: String)
    }
}
