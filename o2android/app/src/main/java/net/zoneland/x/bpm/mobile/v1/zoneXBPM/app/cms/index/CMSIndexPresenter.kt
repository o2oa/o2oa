package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.cms.index

import net.muliba.accounting.app.ExceptionHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.ResponseHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.cms.CMSApplicationInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class CMSIndexPresenter : BasePresenterImpl<CMSIndexContract.View>(), CMSIndexContract.Presenter {

    override fun findAllApplication() {
        getCMSAssembleControlService(mView?.getContext())?.let { service ->
            service.applicationList()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler<List<CMSApplicationInfoJson>> { list -> mView?.loadApplicationSuccess(list) },
                            ExceptionHandler(mView?.getContext()) { e ->
                                XLog.error("", e)
                                mView?.loadApplicationFail()
                            })
        }
    }
}
