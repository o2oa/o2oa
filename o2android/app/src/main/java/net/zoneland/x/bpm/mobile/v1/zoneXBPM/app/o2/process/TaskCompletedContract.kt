package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.process

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.TaskCompleteData


object TaskCompletedContract {
    interface View : BaseView {
        fun findTaskCompletedList(list: List<TaskCompleteData>)
        fun findTaskCompletedListFail()
    }

    interface Presenter : BasePresenter<View> {
        fun findTaskCompletedList(applicationId:String, lastId:String, limit:Int)
    }
}
