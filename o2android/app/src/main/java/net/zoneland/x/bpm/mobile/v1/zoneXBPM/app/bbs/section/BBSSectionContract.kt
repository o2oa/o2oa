package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.bbs.section

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.bbs.SubjectInfoJson


object BBSSectionContract {
    interface View : BaseView {
        fun collectOrCancelCollectSectionResponse(result:Boolean, message:String)
        fun hasBeenCollected(result:Boolean)
        fun loadFail(message: String)
        fun loadSuccess(list: List<SubjectInfoJson>)
        fun publishPermission(result: Boolean)
    }

    interface Presenter : BasePresenter<View> {
        fun canPublishSubject(sectionId:String)
        fun whetherTheSectionHasBeenCollected(sectionId: String)
        fun collectOrCancelCollectSection(sectionId: String, isCollected:Boolean)
        fun loadSubjectList(sectionId: String, pagerNumber:Int)
    }
}
