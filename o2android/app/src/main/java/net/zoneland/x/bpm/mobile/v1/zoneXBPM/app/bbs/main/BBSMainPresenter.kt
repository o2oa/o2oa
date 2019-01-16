package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.bbs.main

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.realm.RealmDataService
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


class BBSMainPresenter : BasePresenterImpl<BBSMainContract.View>(), BBSMainContract.Presenter {

    override fun whetherThereHasCollections() {
        mView?.let {
            RealmDataService().hasAnyBBSCollection()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ aBoolean ->
                        it.whetherThereHasAnyCollections(aBoolean)
                    }, { e ->
                        XLog.error("", e)
                        it.whetherThereHasAnyCollections(false)
                    })
        }
    }
}
