package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.main

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.person.PersonJson
import java.io.File

/**
 * Created by fancylou on 10/9/17.
 */
object MyContract {

    interface View : BaseView {
        fun loadMyInfoSuccess(personal: PersonJson)
        fun loadMyInfoFail()
        fun updateMyInfoFail()
        fun updateMyIcon(f: Boolean)
    }

    interface Presenter : BasePresenter<View> {
        fun loadMyInfo()
        fun updateMyInfo(personal: PersonJson)
        fun updateMyIcon(file: File)

    }
}