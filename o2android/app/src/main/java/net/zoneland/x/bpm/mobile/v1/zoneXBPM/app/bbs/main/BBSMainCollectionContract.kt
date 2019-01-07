package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.bbs.main

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.BBSCollectionSectionVO


object BBSMainCollectionContract {
    interface View : BaseView {
        fun queryAllMyCollectionsResponse(list: List<BBSCollectionSectionVO>)
        fun queryAllMyCollectionsResponseError()
        fun mustSelectMoreThanOne()
        fun cancelCollectionResponse(flag: Boolean)
    }

    interface Presenter : BasePresenter<View> {
        fun queryAllMyBBSCollections()
        fun cancelSomeCollections(mSelectIds: HashSet<String>)
    }
}
