package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.security

import android.text.TextUtils
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by fancyLou on 2019-05-07.
 * Copyright © 2019 O2. All rights reserved.
 */


class DeviceManagerPresenter: BasePresenterImpl<DeviceManagerContract.View>(),DeviceManagerContract.Presenter {
    override fun unbind(unbindToken: String) {
        val service = getCollectService(mView?.getContext())
        if (service == null) {
            mView?.unbindBack(false, "服务不存在！")
            return
        }
        if (TextUtils.isEmpty(unbindToken)) {
            mView?.unbindBack(false, "没有获取到设备Id，无法解绑！")
            return
        }
        service.unBindDevice(unbindToken).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .o2Subscribe {
                    onNext {
                        mView?.unbindBack(true, "")
                    }
                    onError { e, _ ->
                        mView?.unbindBack(false, "解绑失败 ${e?.message ?: ""}")
                    }
                }
    }

    override fun listDevice() {
        val service = getCollectService(mView?.getContext())
        if (service == null) {
            mView?.list(ArrayList())
            return
        }
        val unit = O2SDKManager.instance().prefs().getString(O2.PRE_BIND_UNIT_ID_KEY, "")
        val phone = O2SDKManager.instance().prefs().getString(O2.PRE_BIND_PHONE_KEY, "")
        val token = O2SDKManager.instance().prefs().getString(O2.PRE_BIND_PHONE_TOKEN_KEY, "")
        if (TextUtils.isEmpty(unit) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(token)) {
            mView?.list(ArrayList())
            return
        }
        service.getBindDeviceList(unit, phone, token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .o2Subscribe {
                    onNext { res->
                        val list = res.data ?: ArrayList()
                        mView?.list(list)
                    }
                    onError { e, _ ->
                        XLog.error("", e)
                        mView?.list(ArrayList())
                    }
                }
    }



}