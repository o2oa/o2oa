package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.bind

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2CustomStyle
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.edit
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by fancyLou on 17/04/2018.
 * Copyright © 2018 O2. All rights reserved.
 */

class BindPhonePresenter: BasePresenterImpl<BindPhoneContract.View>(), BindPhoneContract.Presenter {
    override fun checkCustomStyle() {
        val url = O2SDKManager.instance().prefs().getString(O2.PRE_CENTER_URL_KEY, "")
        val hash = O2SDKManager.instance().prefs().getString(O2CustomStyle.CUSTOM_STYLE_UPDATE_HASH_KEY, "") ?: ""
        XLog.info("centerUrl:$url, hash:$hash")
        getApiService(mView?.getContext(), url)?.let { api ->
            api.getCustomStyleUpdateDate()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .o2Subscribe {
                        onNext { response->
                            val result = response.data.value
                            if (hash == result) {
                                mView?.customStyle(false)
                            }else{
                                O2SDKManager.instance().prefs().edit {
                                    putString(O2CustomStyle.CUSTOM_STYLE_UPDATE_HASH_KEY, result)
                                }
                                mView?.customStyle(true)
                            }
                        }
                        onError { e, _ ->
                            XLog.error("", e)
                            mView?.customStyle(false)
                        }
                    }
        }
    }
}