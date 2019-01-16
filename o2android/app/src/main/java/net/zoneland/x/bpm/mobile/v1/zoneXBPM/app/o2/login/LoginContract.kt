package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.login

import com.facepp.demo.util.ICamera
import com.megvii.facepp.sdk.Facepp
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.AuthenticationInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.LoginHistoryVO

/**
 * Created by fancy on 2017/6/8.
 */

object LoginContract {
    interface View: BaseView {
        fun getCodeError()
        fun loginSuccess(data: AuthenticationInfoJson)
        fun loginFail()
    }

    interface Presenter: BasePresenter<View> {


        /**
         * 获取验证码
         * @param value 用户名或者手机号码
         */
        fun getVerificationCode(value: String)

        /**
         * 用户名或者手机号码 验证码 登录
         */
        fun login(userName: String, code: String)

        /**
         * 用户名 密码 登录
         */
        fun loginByPassword(userName: String, password: String)

        /**
         * 人脸识别 是否在库里
         */
        fun searchFace(imgData: ByteArray, faces: Array<Facepp.Face> ,mICamera: ICamera, isBackCamera: Boolean)


    }
}