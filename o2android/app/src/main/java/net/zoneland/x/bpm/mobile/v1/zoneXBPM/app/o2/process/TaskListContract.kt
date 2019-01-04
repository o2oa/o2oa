package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.process

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.TaskApplicationData


object TaskListContract {
    interface View : BaseView {
        fun findTaskApplicationList(list: List<TaskApplicationData>)
        fun findTaskApplicationListFail()
    }

    interface Presenter : BasePresenter<View> {
        fun findTaskApplicationList()
    }
}
