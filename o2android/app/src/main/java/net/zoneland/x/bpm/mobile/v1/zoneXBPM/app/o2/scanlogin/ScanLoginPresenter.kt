package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.scanlogin

import android.text.TextUtils
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class ScanLoginPresenter : BasePresenterImpl<ScanLoginContract.View>(), ScanLoginContract.Presenter {

    override fun confirmWebLogin(meta: String) {
        if (TextUtils.isEmpty(meta)) {
            XLog.error("meta is null!")
            mView?.confirmFail()
            return
        }
        getAssembleAuthenticationService(mView?.getContext())?.let { service ->
            service.scanConfirmWebLogin(meta)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .o2Subscribe {
                        onNext { _ ->
                            mView?.confirmSuccess()
                        }
                        onError { e, _ ->
                            XLog.error("", e)
                            mView?.confirmFail()
                        }
                    }

        }
    }
}
