package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.security

import net.muliba.accounting.app.ExceptionHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.ResponseHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.IdData
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class AccountSecurityPresenter : BasePresenterImpl<AccountSecurityContract.View>(), AccountSecurityContract.Presenter {


    override fun logout(deviceId: String) {
        getAssembleAuthenticationService(mView?.getContext())?.let { service->
            service.logout()
                    .subscribeOn(Schedulers.io())
                    .flatMap { getCollectService(mView?.getContext())?.unBindDevice(deviceId) }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler<IdData>({data -> mView?.logoutSuccess()}),
                            ExceptionHandler(mView?.getContext(), { e -> mView?.logoutSuccess()}))
        }
    }
}
