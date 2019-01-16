package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.main

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by fancyLou on 21/03/2018.
 * Copyright © 2018 O2. All rights reserved.
 */


class IndexPortalPresenter : BasePresenterImpl<IndexPortalContract.View>(), IndexPortalContract.Presenter {

    override fun loadCmsCategoryListByAppId(appId: String) {
        getCMSAssembleControlService(mView?.getContext())
                ?.findCategorysByAppId(appId)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.o2Subscribe {
                    onNext {
                        mView?.loadCmsCategoryListByAppId(it.data ?: arrayListOf())
                    }
                    onError { e, _ ->
                        XLog.error("", e)
                        mView?.loadCmsCategoryListByAppId(arrayListOf())
                    }
                }
    }
}