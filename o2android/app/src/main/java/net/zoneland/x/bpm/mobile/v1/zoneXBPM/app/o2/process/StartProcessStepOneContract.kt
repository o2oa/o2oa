package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.process

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.ApplicationData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.ProcessInfoData


object StartProcessStepOneContract {
    interface View : BaseView {
        fun loadApplicationList(list:List<ApplicationData>)
        fun loadApplicationListFail()
        fun loadProcessList(list: List<ProcessInfoData>)
        fun loadProcessListFail()
    }

    interface Presenter : BasePresenter<View> {
        fun loadApplicationList()
        fun loadProcessListByAppId(appId:String)
    }
}
