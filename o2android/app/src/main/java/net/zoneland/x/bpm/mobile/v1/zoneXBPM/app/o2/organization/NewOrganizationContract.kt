package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.organization

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.NewContactListVO


object NewOrganizationContract {
    interface View : BaseView {
        fun callbackResult(list: List<NewContactListVO>)
        fun backError(error:String)
    }

    interface Presenter : BasePresenter<View> {
        fun loadChildrenWithParent(unitParentId: String)
        fun searchPersonWithKey(result: String)
    }
}
