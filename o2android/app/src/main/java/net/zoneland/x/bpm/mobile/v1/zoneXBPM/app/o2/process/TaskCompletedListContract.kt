package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.process

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.TaskApplicationData


object TaskCompletedListContract {
    interface View : BaseView {
        fun findTaskCompletedApplicationList(list: List<TaskApplicationData>)
        fun findTaskCompletedApplicationListFail()

    }

    interface Presenter : BasePresenter<View> {
        fun findTaskCompletedApplicationList()

    }
}
