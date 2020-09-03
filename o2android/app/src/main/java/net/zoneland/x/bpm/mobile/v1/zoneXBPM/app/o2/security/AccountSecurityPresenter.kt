package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.security

import net.muliba.accounting.app.ExceptionHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.ResponseHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.exception.O2ResponseException
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.IdData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.person.PersonPwdForm
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
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

    override fun updateMyPassword(old: String, newPwd: String, newPwdConfirm: String) {
        val service = getAssemblePersonalApi(mView?.getContext())
        if (service != null) {
            val form = PersonPwdForm(old, newPwd, newPwdConfirm)
            service.modifyCurrentPersonPassword(form)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .o2Subscribe {
                        onNext {
                            mView?.updateMyPasswordSuccess()
                        }
                        onError { e, _ ->
                            XLog.error("", e)
                            if (e is O2ResponseException) {
                                e.message?.let { mView?.updateMyPasswordFail(it) }
                            }else {
                                mView?.updateMyPasswordFail("修改失败！")
                            }
                        }
                    }
        }
    }
}
