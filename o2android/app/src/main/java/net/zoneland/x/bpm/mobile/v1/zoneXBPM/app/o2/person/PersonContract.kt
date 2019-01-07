package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.person

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.person.PersonJson


object PersonContract {
    interface View : BaseView {
        fun isUsuallyPerson(flag:Boolean)
        fun loadPersonInfo(personInfo: PersonJson)
        fun loadPersonInfoFail()
    }

    interface Presenter : BasePresenter<View> {
        fun loadPersonInfo(name:String)
        fun collectionUsuallyPerson(owner:String, person:String, ownerDisplay:String,personDisplay:String, gender:String, mobile:String)
        fun deleteUsuallyPerson(owner: String, person: String)
        fun isUsuallyPerson(owner: String, person: String)
    }
}
