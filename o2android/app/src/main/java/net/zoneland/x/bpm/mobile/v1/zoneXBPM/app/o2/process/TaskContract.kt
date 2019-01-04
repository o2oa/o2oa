package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.process

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.TaskData


object TaskContract {
    interface View : BaseView {
        fun findTaskList(list: List<TaskData>)
        fun findTaskListFail()
    }

    interface Presenter : BasePresenter<View> {
        fun findTaskList(applicationId:String, lastId:String, limit:Int)

    }
}
