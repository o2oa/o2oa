package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.main

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.realm.RealmDataService
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by fancy on 2017/6/9.
 * Copyright © 2017 O2. All rights reserved.
 */

class SettingsPresenter: BasePresenterImpl<SettingsContract.View>(), SettingsContract.Presenter {

    val service: RealmDataService by lazy {  RealmDataService() }


    override fun logout() {
        getAssembleAuthenticationService(mView?.getContext())?.let { service->
                    service.logout()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ _ ->  mView?.logoutSuccess() }, { e ->
                        XLog.error("", e)
                        mView?.logoutFail()
                    })
        }
    }

    override fun cleanApp() {
        service.deleteALlNativeApp().subscribeOn(Schedulers.io())
                .flatMap {
                    service.deleteAllPortal()
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { mView?.cleanOver() }
    }
}