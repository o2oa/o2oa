package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.group

import android.widget.TextView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView


object GroupContract {
    interface View : BaseView {
        fun loadGroupMembers(members:List<String>)
    }

    interface Presenter : BasePresenter<View> {
        fun loadGroupMembers(groupName:String)
        fun asyncLoadPersonMobileAndDepartment(person:String?, mobileTv:TextView?, deptTv:TextView?)
    }
}
