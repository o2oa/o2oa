package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.my

import net.muliba.accounting.app.ExceptionHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.ResponseHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.ValueData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.person.PersonJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File

class MyInfoPresenter : BasePresenterImpl<MyInfoContract.View>(), MyInfoContract.Presenter {

    override fun loadMyInfo() {
        getAssemblePersonalApi(mView?.getContext())?.let { service->
            service.getCurrentPersonInfo()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ data -> mView?.loadMyInfoSuccess(data.data) }, { e ->
                        XLog.error("", e)
                        mView?.loadMyInfoFail()
                    })
        }
    }

    override fun updateMyInfo(personal: PersonJson) {
        getAssemblePersonalApi(mView?.getContext())?.let { service->
            service.modifyCurrentPerson(personal)
                    .subscribeOn(Schedulers.io())
                    .flatMap { service.getCurrentPersonInfo() }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler<PersonJson>({ data -> mView?.loadMyInfoSuccess(data) }),
                            ExceptionHandler(mView?.getContext(), { e ->
                                mView?.updateMyInfoFail()
                            }))
        }
    }

    override fun updateMyIcon(file: File) {
            val requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), file)
            val body = MultipartBody.Part.createFormData("file", file.name, requestBody)
            getAssemblePersonalApi(mView?.getContext())?.let { service->
                service.modifyCurrentPersonIcon(body)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler<ValueData> { value -> mView?.updateMyIcon(value.isValue) },
                            ExceptionHandler(mView?.getContext()) { e -> mView?.updateMyIcon(false) })
        }
    }

    override fun logout() {
        getAssembleAuthenticationService(mView?.getContext())?.let { service->
            service.logout()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ _ -> mView?.logoutSuccess() }, { e ->
                        XLog.error("", e)
                        mView?.logoutFail()
                    })
        }
    }
}
