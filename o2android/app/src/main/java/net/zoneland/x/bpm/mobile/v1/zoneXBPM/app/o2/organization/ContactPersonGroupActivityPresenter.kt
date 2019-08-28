package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.organization

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.NewContactListVO
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by fancyLou on 2019-08-09.
 * Copyright © 2019 O2. All rights reserved.
 */


class ContactPersonGroupActivityPresenter: BasePresenterImpl<ContactPersonGroupActivityContract.View>(), ContactPersonGroupActivityContract.Presenter {
    override fun findListByPage(mode: String, lastId: String) {
        val service =  getOrganizationAssembleControlApi(mView?.getContext())
        if (service == null) {
            mView?.backError("组织模块异常")
            return
        }
        if (mode == ContactPersonGroupPicker.GROUP_PICK_MODE) {
            service.groupListByPage(lastId, O2.DEFAULT_PAGE_NUMBER).subscribeOn(Schedulers.io())
                    .flatMap { response ->
                        val retList = ArrayList<NewContactListVO>()
                        val list = response.data
                        if (list != null && list.isNotEmpty()) {
                            list.map { retList.add(it.copy2NewContactListVO()) }
                        }
                        Observable.just(retList)
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .o2Subscribe {
                        onNext {
                            mView?.callbackResult(it)
                        }
                        onError { e, isNetworkError ->
                            XLog.error("$isNetworkError ", e)
                            mView?.backError("查询数据异常")
                        }
                    }

        }else {
            service.personListByPage(lastId, O2.DEFAULT_PAGE_NUMBER).subscribeOn(Schedulers.io())
                    .flatMap { response ->
                        val retList = ArrayList<NewContactListVO>()
                        val list = response.data
                        if (list != null && list.isNotEmpty()) {
                            list.map { retList.add(it.copy2NewContactListVO()) }
                        }
                        Observable.just(retList)
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .o2Subscribe {
                        onNext {
                            mView?.callbackResult(it)
                        }
                        onError { e, isNetworkError ->
                            XLog.error("$isNetworkError ", e)
                            mView?.backError("查询数据异常")
                        }
                    }
        }
    }

}