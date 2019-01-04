package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.bbs.main

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.group.Group
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.bbs.SectionInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.BBSCollectionSectionVO


object BBSMainSectionContract {
    interface View : BaseView {
        fun loadFail()
        fun loadSuccess(items: List<Group<String, SectionInfoJson>>)
        fun queryAllMyCollectionsResponse(list: List<BBSCollectionSectionVO>)
        fun queryAllMyCollectionsResponseError()
    }

    interface Presenter : BasePresenter<View> {
        fun queryAllMyBBSCollections()
        fun loadForumList()
    }
}
