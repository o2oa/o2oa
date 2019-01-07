package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.process

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.TaskCompleteData


object TaskCompletedSearchContract {
    interface View : BaseView {
        fun searchFail()
        fun searchResult(list: List<TaskCompleteData>)
    }

    interface Presenter : BasePresenter<View> {
        fun searchTaskCompleted(lastId:String, key:String)
    }
}
