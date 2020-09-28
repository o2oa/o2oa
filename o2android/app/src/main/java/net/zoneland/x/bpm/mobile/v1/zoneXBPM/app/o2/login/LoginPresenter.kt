package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.login


import android.text.TextUtils
import net.muliba.accounting.app.ExceptionHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.ResponseHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.LoginWithCaptchaForm
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.Base64ImageUtil
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.CryptRSA
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by fancy on 2017/6/8.
 */

class LoginPresenter : BasePresenterImpl<LoginContract.View>(), LoginContract.Presenter {

    //rsa加密公钥
    private var publicKey: String = ""


    override fun getLoginMode() {
        getAssembleAuthenticationService(mView?.getContext())?.let { service ->
            service.loginMode()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .o2Subscribe {
                        onNext {
                            mView?.loginMode(it.data)
                        }
                        onError { e, _ ->
                            XLog.error("", e)
                            mView?.loginMode(null)
                        }
                    }
        }
    }

    override fun getCaptcha() {
        getAssembleAuthenticationService(mView?.getContext())?.let { service ->
            service.getCaptchaCodeImg(120, 50)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .o2Subscribe {
                        onNext {
                            if (it.data != null) {
                                mView?.showCaptcha(it.data)
                            }else {
                                mView?.getCaptchaError("没有获取到图片验证码")
                            }
                        }
                        onError { e, _ ->
                            XLog.error("获取图片验证码错误,", e)
                            mView?.getCaptchaError("没有获取到图片验证码")
                        }
                    }
        }
    }

    override fun loginWithCaptcha(form: LoginWithCaptchaForm) {
        getAssembleAuthenticationService(mView?.getContext())?.let { service ->
            //加密
            if (!TextUtils.isEmpty(publicKey)) {
                XLog.debug("key：$publicKey")
                val newPwd = CryptRSA.rsaEncryptByPublicKey(form.password, publicKey)
                if (!TextUtils.isEmpty(newPwd)) {
                    form.password = newPwd
                    form.isEncrypted = "y"
                    XLog.debug("加密成功。。。。。$newPwd")
                }
            }
            service.loginWithCaptchaCode(form)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler { data -> mView?.loginSuccess(data) },
                            ExceptionHandler(mView?.getContext()) { e ->
                                XLog.error("", e)
                                mView?.loginFail()
                            })
        }
    }

    override fun getRSAPublicKey() {
        getAssembleAuthenticationService(mView?.getContext())?.let { service ->
            service.getRSAPublicKey()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .o2Subscribe {
                        onNext {
                            if (it.data!=null) {
                                publicKey = it.data.publicKey
                                XLog.debug("public key is ok.lllll ")
                            }
                        }
                        onError { e, _ ->
                            XLog.error("public key is error ", e)
                        }
                    }
        }
    }

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