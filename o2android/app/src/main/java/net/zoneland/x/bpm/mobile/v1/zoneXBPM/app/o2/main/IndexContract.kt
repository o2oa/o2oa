package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.main

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.cms.CMSDocumentInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.HotPictureOutData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.TaskData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.persistence.MyAppListObject

/**
 * Created by fancy on 2017/6/9.
 */

object IndexContract {
    interface View: BaseView{
        fun loadTaskList(list: List<TaskData>)
        fun loadTaskListFail()
        fun loadNewsList(list: List<CMSDocumentInfoJson>)
        fun loadNewsListFail()
        fun loadHotPictureList(list: List<HotPictureOutData>)
        fun loadHotPictureListFail()
        fun setMyAppList(myAppList: ArrayList<MyAppListObject>)

    }
    interface Presenter: BasePresenter<View> {

        fun loadTaskList(lastId: String)
        fun loadNewsList(lastId: String)
        fun loadHotPictureList()
        fun getMyAppList()

    }
}