package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.im

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


/**
 * Created by fancyLou on 2020-05-25.
 * Copyright © 2020 O2. All rights reserved.
 */

class O2InstantMessagePresenter : BasePresenterImpl<O2InstantMessageContract.View>(), O2InstantMessageContract.Presenter  {
    override fun getWorkInfo(workId: String) {
        val service = getProcessAssembleSurfaceServiceAPI(mView?.getContext())
        if (service != null) {
            service.getWorkInfo(workId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .o2Subscribe {
                        onNext {
                            mView?.workIsCompleted(false, workId)
                        }
                        onError { e, _ ->
                            XLog.error("", e)
                            mView?.workIsCompleted(true, workId)
                        }
                    }
        }else {
            mView?.workIsCompleted(true, workId)
        }
    }

}