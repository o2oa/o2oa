package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.login

import com.facepp.demo.util.ConUtil
import com.facepp.demo.util.ICamera
import com.megvii.facepp.sdk.Facepp
import net.muliba.accounting.app.ExceptionHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.APIAddressHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.ResponseHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.Base64ImageUtil
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File
import java.util.ArrayList

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




    private var lock: Boolean = false
    override fun searchFace(imgData: ByteArray, faces: Array<Facepp.Face>, mICamera: ICamera, isBackCamera: Boolean) {
        if (lock) {
            XLog.info("search ing ..............")
            return
        }
        lock = true
        val list = saveFaceAsFile(faces, mICamera, imgData, isBackCamera)
        if (list.isNotEmpty()) {
            val filePath = list[0]
            val file = File(filePath)
            if (file.exists()) {
                val baseUrl = APIAddressHelper.instance().getFaceppServerUrl()
                val faceset = O2SDKManager.instance().prefs().getString(O2.PRE_BIND_UNIT_ID_KEY, "")
                XLog.debug("baseUrl:$baseUrl ,faceset:$faceset")
                val requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), file)
                val body = MultipartBody.Part.createFormData("file", file.name, requestBody)
                val service = getFaceppService(baseUrl, mView?.getContext())
                val ssoService = getAssembleAuthenticationService(mView?.getContext())
                if (service!=null) {
                    service.searchFace(body, "dev_o2oa_io")
                            .subscribeOn(Schedulers.io())
                            .flatMap { res->
                                val data = res.data
                                if (data!=null && data.results.isNotEmpty()) {
                                    val userid = data.results[0].user_id
                                    val enCode = Base64ImageUtil.ssoUserIdDesCode(userid)
                                    XLog.info("识别成功，userId:$userid, 加密后：$enCode")
                                    if (ssoService!=null) {
                                        val jsonMap = HashMap<String, String>()
                                        jsonMap["client"] = O2.O2_CLIENT
                                        jsonMap["token"] = enCode
                                        ssoService.sso(jsonMap)
                                    }else{
                                        throw Exception("")
                                    }
                                }else{
                                    throw Exception("没有识别到这个人脸")
                                }
                            }
                            .observeOn(AndroidSchedulers.mainThread())
                            .o2Subscribe {
                                onNext { res->
                                    if (res != null && res.data != null) {
                                        mView?.loginSuccess(res.data)
                                        lock = true//不再执行searchFace了
                                    }else {
                                        XLog.error("没有登录成功的信息。。。。。。。")
                                        lock = false
                                    }
                                }
                                onError { e, isNetworkError ->
                                    XLog.error("没有识别到, $isNetworkError", e)
                                    lock = false
                                }

                            }
                }else{
                    XLog.error("人脸识别服务不存在。。。")
                    lock = false
                }
            }else {
                XLog.error("生成的人脸识别文件不存在。。。。")
                lock = false
            }
        }else {
            XLog.error("生成的人脸识别文件不存在。。。。")
            lock = false
        }

    }

    private fun saveFaceAsFile(faces: Array<Facepp.Face>, mICamera: ICamera, carmeraImgData: ByteArray, isBackCamera: Boolean): List<String> {
        val imgs = ArrayList<String>()
        for (i in faces.indices) {
            val face = faces[i]
            val rect = face.rect
            val bitmap = mICamera.getBitMapWithRect(carmeraImgData, mICamera.mCamera, !isBackCamera, rect)
            if (bitmap != null) {
                val filePath = ConUtil.saveBitmap(mView?.getContext(), bitmap)
                XLog.info("file path:$filePath")
                imgs.add(filePath)
            }
        }
        return imgs
    }
}