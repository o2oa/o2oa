package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.openim

import android.widget.TextView
import net.muliba.accounting.app.ExceptionHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.ResponseHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by fancyLou on 2018/2/1.
 * Copyright © 2018 O2. All rights reserved.
 */

class IMTribeInfoActivityPresenter: BasePresenterImpl<IMTribeInfoContract.View>() ,IMTribeInfoContract.Presenter {

    override fun asyncLoadPersonName(tv: TextView, personId: String) {
        getOrganizationAssembleControlApi(mView?.getContext())?.let { service ->
            service.person(personId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler { person ->
                        XLog.debug("person id:${person.id}, name:${person.name}")
                        val tag = tv.tag as String
                        if (person.id == tag) {
                            tv.text = person.name
                        }
                    }, ExceptionHandler(mView?.getContext()) { e ->
                        XLog.error("查询个人信息失败, id:${personId}", e)
                    })
        }
    }
}