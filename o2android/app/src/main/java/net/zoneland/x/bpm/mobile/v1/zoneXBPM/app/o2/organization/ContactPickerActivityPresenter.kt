package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.organization

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.AndroidUtils
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by fancyLou on 2019-08-20.
 * Copyright © 2019 O2. All rights reserved.
 */

class ContactPickerActivityPresenter: BasePresenterImpl<ContactPickerActivityContract.View>(), ContactPickerActivityContract.Presenter {
    override fun getPerson(dn: String, type: String) {
        XLog.debug("getPerson dn:$dn, type:$type")
        getOrganizationAssembleControlApi(mView?.getContext())?.person(dn)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.o2Subscribe {
                    onNext {
                        val person = it?.data
                        if (person != null) {
                            mView?.setPersonInfo(person, type)
                        }else {
                            XLog.error("没有查询到用户信息，$dn")
                        }
                    }
                    onError { e, isNetworkError ->
                        XLog.error("net: $isNetworkError", e)
                    }
                }
    }

}