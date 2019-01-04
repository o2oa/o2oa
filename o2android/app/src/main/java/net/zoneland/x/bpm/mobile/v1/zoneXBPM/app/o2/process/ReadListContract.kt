package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.process

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.ReadData


object ReadListContract {
    interface View : BaseView {
        fun returnReadList(list: List<ReadData>)
        fun finishLoading()
    }

    interface Presenter : BasePresenter<View> {
        fun findReadList(applicationId:String, lastId:String, limit:Int)
    }
}
