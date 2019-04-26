package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.login


import net.muliba.accounting.app.ExceptionHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.ResponseHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.Base64ImageUtil
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by fancy on 2017/6/8.
 */

class LoginPresenter : BasePresenterImpl<LoginContract.View>(), LoginContract.Presenter {



    override fun getVerificationCode(value: String) {
        getAssembleAuthenticationService(mView?.getContext())?.let { service ->
            service.getPhoneCode(value)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler { _ -> },
                            ExceptionHandler(mView?.getContext()) { e ->
                                XLog.error("", e)
                                mView?.getCodeError()
                            })
        }
    }

    override fun login(userName: String, code: String) {
        val params: HashMap<String, String> = HashMap()
        params["credential"] = userName
        params["codeAnswer"] = code
        getAssembleAuthenticationService(mView?.getContext())?.let { service ->
            service.loginWithPhoneCode(params)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler { data -> mView?.loginSuccess(data) },
                            ExceptionHandler(mView?.getContext()) { e ->
                                XLog.error("", e)
                                mView?.loginFail()
                            })
        }
    }

    override fun loginByPassword(userName: String, password: String) {
        val params: HashMap<String, String> = HashMap()
        params["credential"] = userName
        params["password"] = password
        getAssembleAuthenticationService(mView?.getContext())?.let { service ->
            service.login(params)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler { data -> mView?.loginSuccess(data) },
                            ExceptionHandler(mView?.getContext()) { e ->
                                XLog.error("", e)
                                mView?.loginFail()
                            })
        }
    }





    override fun ssoLogin(userId: String) {
        val ssoService = getAssembleAuthenticationService(mView?.getContext())
        val enCode = Base64ImageUtil.ssoUserIdDesCode(userId)
        XLog.info("识别成功，userId:$userId, 加密后：$enCode")
        if (ssoService!=null) {
            val jsonMap = HashMap<String, String>()
            jsonMap["client"] = O2.O2_CLIENT
            jsonMap["token"] = enCode
            ssoService.sso(jsonMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .o2Subscribe {
                        onNext { res->
                            if (res != null && res.data != null) {
                                mView?.loginSuccess(res.data)
                            }else {
                                XLog.error("没有登录成功的信息。。。。。。。")
                                mView?.loginFail()
                            }
                        }
                        onError { e, isNetworkError ->
                            XLog.error("没有识别到, $isNetworkError", e)
                            mView?.loginFail()
                        }

                    }
        }else {
            mView?.loginFail()
        }
    }


}