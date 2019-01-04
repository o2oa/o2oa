package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.process

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.ReadCompleteData


object ReadCompletedListContract {
    interface View : BaseView {
        fun returnReadCompletedList(list: List<ReadCompleteData>)
        fun finishLoading()
    }

    interface Presenter : BasePresenter<View> {
        fun findReadCompletedList(applicationId:String, lastId:String, limit:Int)
    }
}
