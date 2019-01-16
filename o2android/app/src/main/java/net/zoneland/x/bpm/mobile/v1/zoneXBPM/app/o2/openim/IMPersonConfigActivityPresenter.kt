package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.openim

import net.muliba.accounting.app.ExceptionHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.ResponseHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.person.PersonJson
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by fancyLou on 2018/1/31.
 * Copyright © 2018 O2. All rights reserved.
 */


class IMPersonConfigActivityPresenter: BasePresenterImpl<IMPersonConfigContract.View>(), IMPersonConfigContract.Presenter {

    override fun loadPersonInfo(personId: String) {
        getOrganizationAssembleControlApi(mView?.getContext())?.let { service ->
            service.person(personId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler<PersonJson>({ person -> mView?.loadPersonInfo(person) }),
                            ExceptionHandler(mView?.getContext(), { e -> mView?.loadPersonInfoFail() }))
        }
    }
}