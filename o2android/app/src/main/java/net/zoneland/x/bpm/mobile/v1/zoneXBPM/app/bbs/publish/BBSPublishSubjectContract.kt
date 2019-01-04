package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.bbs.publish

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.bbs.SectionInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.bbs.SubjectPublishFormJson


object BBSPublishSubjectContract {
    interface View : BaseView {
        fun sectionInfo(info: SectionInfoJson)
        fun querySectionFail()
        fun publishFail()
        fun publishSuccess(id:String)
        fun uploadFail(tag:String)
        fun uploadSuccess(fileId:String, tag:String)
    }

    interface Presenter : BasePresenter<View> {
        fun querySectionById(sectionId:String)
        fun publishSubject(form: SubjectPublishFormJson)
        fun uploadImage(filePath:String, subjectId: String)

    }
}
