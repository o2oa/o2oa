package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.bbs.main

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.realm.RealmDataService
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class BBSMainCollectionPresenter : BasePresenterImpl<BBSMainCollectionContract.View>(), BBSMainCollectionContract.Presenter {

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

    override fun cancelSomeCollections(mSelectIds: HashSet<String>) {
        mView?.let {
            if (mSelectIds.size < 1) {
                it.mustSelectMoreThanOne()
                return
            }
            RealmDataService().deleteBBSCollections(mSelectIds.map { it })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ _ -> it.cancelCollectionResponse(true) },
                            { e ->
                                XLog.error("", e)
                                it.cancelCollectionResponse(false)
                            })
        }
    }
}
