package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.bind

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.APIDistributeData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.AuthenticationInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.CollectUnitData

/**
 * Created by fancy on 2017/6/8.
 */

object SecondStepContract {
    interface View: BaseView {
        fun bindSuccess(distributeData: APIDistributeData)
        fun bindFail()
        fun noDeviceId()

        fun loginSuccess(data: AuthenticationInfoJson)
        fun loginFail()

    }
    interface Presenter: BasePresenter<View> {
        fun bindDevice(deviceId:String, phone:String, code: String, unitData: CollectUnitData)
        /**
         * 登录
         */
        fun login(userName: String, code: String)

    }
}