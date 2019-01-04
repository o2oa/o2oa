package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.bbs.main

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.group.Group
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.realm.RealmDataService
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.bbs.SectionInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class BBSMainSectionPresenter : BasePresenterImpl<BBSMainSectionContract.View>(), BBSMainSectionContract.Presenter {

    override fun loadForumList() {
        getBBSAssembleControlService(mView?.getContext())?.let {service->
            service.forumAll()
                    .subscribeOn(Schedulers.io())
                    .flatMap { response ->
                        Observable.create(object : Observable.OnSubscribe<List<Group<String, SectionInfoJson>>> {
                            override fun call(t: Subscriber<in List<Group<String, SectionInfoJson>>>?) {
                                val items = ArrayList<Group<String, SectionInfoJson>>()
                                try {
                                    response.data.filter { it.sectionInfoList.isNotEmpty() }.map { items.add(Group(it.forumName, it.sectionInfoList)) }
                                    t?.onNext(items)
                                } catch (e: Exception) {
                                    t?.onError(e)
                                }
                                t?.onCompleted()
                            }
                        })
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ list -> mView?.loadSuccess(list) }, { e ->
                        XLog.error("", e)
                        mView?.loadFail()
                    })
        }
    }

    override fun queryAllMyBBSCollections() {
        mView?.let {
            RealmDataService().findBBSCollectionAll()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ list ->
                        it.queryAllMyCollectionsResponse(list)
                    }, { e ->
                        XLog.error("", e)
                        it.queryAllMyCollectionsResponseError()
                    })
        }

    }
}
