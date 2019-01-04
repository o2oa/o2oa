package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.main

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.persistence.MyAppListObject

/**
 * Created by fancy on 2017/6/8.
 */


object MyAppContract {
    interface View : BaseView {
        fun setAllAppList(allList: ArrayList<MyAppListObject>)
        fun setMyAppList(myAppList: ArrayList<MyAppListObject>)
        fun addAndDelMyAppList(isSuccess: Boolean)
    }

    interface Presenter: BasePresenter<View> {
        fun getAllAppList()
        fun getMyAppList()
        fun addAndDelMyAppList(delAppList: ArrayList<MyAppListObject>,addAppList: ArrayList<MyAppListObject>)
    }
}