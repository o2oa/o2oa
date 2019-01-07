package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.bind

import android.text.TextUtils
import net.muliba.accounting.app.ExceptionHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2App
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.APIAddressHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.ResponseHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.APIDistributeData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.AuthenticationInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.CollectCodeData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.CollectDeviceData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.CollectUnitData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.edit
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Action1
import rx.schedulers.Schedulers

/**
 * Created by fancy on 2017/6/8.
 */

class FirstStepPresenter : BasePresenterImpl<FirstStepContract.View>(), FirstStepContract.Presenter {

    override fun getVerificationCode(phoneNumber: String) {
        mView?.let {
            val postBody = CollectCodeData()
            postBody.mobile = phoneNumber
            getCollectService(it.getContext())?.let { service ->
                service.getCode(postBody)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(ResponseHandler<CollectCodeData> {
                            XLog.info("发送短信验证码成功，$phoneNumber， 验证码：${it.value} , meta:${it.meta}")
                        },
                                ExceptionHandler(it.getContext(), { e -> XLog.error("", e) }))
            }
        }
    }

    override fun getUnitList(phone: String, code: String) {
        getCollectService(mView?.getContext())?.let { service ->
            service.getUnitList(phone, code)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler { data -> mView?.receiveUnitList(data) },
                            Action1<Throwable> { e ->
                                XLog.error("", e)
                                mView?.receiveUnitFail()

                            })
        }
    }

    override fun bindDevice(deviceId: String, phone: String, code: String, unitData: CollectUnitData) {
        XLog.debug("bindDevice, token：$deviceId, unit:$unitData, phone:$phone, code:$code")

        if (TextUtils.isEmpty(deviceId)) {
            mView?.noDeviceId()
            return
        }
        val postBody = CollectDeviceData()
        postBody.name = deviceId
        postBody.unit = unitData.name
        postBody.code = code
        postBody.mobile = phone
        postBody.deviceType = O2.DEVICE_TYPE
        val url = APIAddressHelper.instance().getCenterUrl(unitData.centerHost,
                unitData.centerContext, unitData.centerPort)

        getCollectService(mView?.getContext())?.let { service ->
            service.bindDevice(postBody)
                    .subscribeOn(Schedulers.io())
                    .flatMap{
                        getApiService(mView?.getContext(), url)?.getWebserverDistributeWithSource(unitData.centerHost)
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler { data ->
                        //绑定成功写入本地存储
                        O2SDKManager.instance().bindUnit(unitData, phone, deviceId)
                        mView?.bindSuccess(data)
                    }, Action1<Throwable> { e ->
                        XLog.error("", e)
                        mView?.bindFail()
                    })
        }

    }

    override fun login(userName: String, code: String) {
        val params: HashMap<String, String> = HashMap()
        params.put("credential", userName)
        params.put("codeAnswer", code)
        getAssembleAuthenticationService(mView?.getContext())?.let { service ->
            service.loginWithPhoneCode(params)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler { data -> mView?.loginSuccess(data) },
                            ExceptionHandler(mView?.getContext()) { _ -> mView?.loginFail() })
        }
    }
}