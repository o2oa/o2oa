package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.bind

import android.text.TextUtils
import android.view.View
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_fluid_login_phone.*
import net.muliba.changeskin.FancySkinManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2App
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPFragment
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.APIAddressHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.RetrofitClient
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.APIDistributeData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.AuthenticationInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.CollectUnitData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.StringUtil
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.hideSoftInput
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.CountDownButtonHelper

/**
 * Created by fancy on 2017/6/8.
 */


class FirstStepFragment : BaseMVPFragment<FirstStepContract.View, FirstStepContract.Presenter>(), FirstStepContract.View, View.OnClickListener {

    var phone = ""
    var code = ""
    val countDownHelper: CountDownButtonHelper by lazy { CountDownButtonHelper(button_login_phone_code, getString(R.string.login_button_code), 60, 1) }

    override var mPresenter: FirstStepContract.Presenter = FirstStepPresenter()
    override fun layoutResId(): Int = R.layout.fragment_fluid_login_phone

    override fun initUI() {
        countDownHelper.setOnFinishListener { XLog.debug("CountDownButtonHelper, finish.................."); }
        button_login_phone_next.setOnClickListener(this)
        button_login_phone_code.setOnClickListener(this)
        edit_login_phone.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                view_bind_phone_step_one_number_bottom.setBackgroundColor(FancySkinManager.instance().getColor(activity, R.color.z_color_input_line_focus))
                image_login_phone_icon.setImageDrawable(FancySkinManager.instance().getDrawable(activity, R.mipmap.icon_phone_focus))
            } else {
                view_bind_phone_step_one_number_bottom.setBackgroundColor(FancySkinManager.instance().getColor(activity, R.color.z_color_input_line_blur))
                image_login_phone_icon.setImageDrawable(FancySkinManager.instance().getDrawable(activity, R.mipmap.icon_phone_normal))
            }


        }
        edit_login_code.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                view_bind_phone_step_one_code_bottom.setBackgroundColor(FancySkinManager.instance().getColor(activity, R.color.z_color_input_line_focus))
                image_login_phone_code_icon.setImageDrawable(FancySkinManager.instance().getDrawable(activity, R.mipmap.icon_verification_code_focus))
            } else {
                view_bind_phone_step_one_code_bottom.setBackgroundColor(FancySkinManager.instance().getColor(activity, R.color.z_color_input_line_blur))
                image_login_phone_code_icon.setImageDrawable(FancySkinManager.instance().getDrawable(activity, R.mipmap.icon_verification_code_normal))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownHelper.destroy()
    }

    override fun receiveUnitList(list: List<CollectUnitData>) {
        if (list.size == 1) {
            autoBind(list[0])
        }else {
            val json = O2SDKManager.instance().gson.toJson(list, object : TypeToken<List<CollectUnitData>>(){}.type)
            redirectToSecondStep(json)
        }
    }

    override fun receiveUnitFail() {
        XToast.toastShort(activity, "没有获取到绑定服务器列表！")
    }

    override fun bindSuccess(distributeData: APIDistributeData) {
        APIAddressHelper.instance().setDistributeData(distributeData)

        gotoLogin()
    }

    override fun bindFail() {
        hideLoadingDialog()
        XToast.toastShort(activity, "绑定服务器失败！")
    }

    override fun noDeviceId() {
        hideLoadingDialog()
        XToast.toastShort(activity, "没有获取到您的设备号，请检查应用的权限设置！")
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.button_login_phone_next -> {
                val phone = edit_login_phone.text.toString()
                val code = edit_login_code.text.toString()
                if (TextUtils.isEmpty(phone)) {
                    XToast.toastShort(activity, "请输入手机号码！")
                    return
                }
                if (!StringUtil.isPhoneWithHKandMACAO(phone)) {
                    XToast.toastShort(activity, "请输入正确的手机号码！")
                    return
                }
                if (TextUtils.isEmpty(code)) {
                    XToast.toastShort(activity, "短信验证码不能为空！")
                    return
                }
                activity.hideSoftInput()
                this.phone = phone
                this.code = code
                mPresenter.getUnitList(phone, code)
            }
            R.id.button_login_phone_code -> {
                val phone = edit_login_phone.text.toString()
                if (TextUtils.isEmpty(phone)) {
                    XToast.toastShort(activity, "请输入手机号码！")
                    return
                }
                if (!StringUtil.isPhoneWithHKandMACAO(phone)) {
                    XToast.toastShort(activity, "请输入正确的手机号码！")
                    return
                }
                // 发送验证码
                mPresenter.getVerificationCode(phone)
                countDownHelper.destroy()
                countDownHelper.start()

                //焦点跳转到验证码上面
                edit_login_code.setText("")//先清空
                edit_login_code.isFocusable = true
                edit_login_code.isFocusableInTouchMode = true
                edit_login_code.requestFocus()
                edit_login_code.requestFocusFromTouch()
            }
            else -> XLog.error("no implements this view ,id:${v?.id}")
        }

    }

    override fun loginSuccess(data: AuthenticationInfoJson) {
        O2SDKManager.instance().setCurrentPersonData(data)
        hideLoadingDialog()
        (activity as BindPhoneActivity).startInstallCustomStyle(true)
    }

    override fun loginFail() {
        hideLoadingDialog()
        //自动登陆失败 跳转过去手动登陆
        (activity as BindPhoneActivity).startInstallCustomStyle(false, phone)
    }

    private fun autoBind(collectUnitData: CollectUnitData) {
        XLog.debug("autoBind......${collectUnitData.name}")
        showLoadingDialog()
        //更新http协议
        RetrofitClient.instance().setO2ServerHttpProtocol(collectUnitData.httpProtocol)
        APIAddressHelper.instance().setHttpProtocol(collectUnitData.httpProtocol)
        mPresenter.bindDevice((activity as BindPhoneActivity).loadDeviceId(), phone, code, collectUnitData)
    }

    private fun redirectToSecondStep(json: String?) {
        if (TextUtils.isEmpty(json)){
            XToast.toastShort(activity, "绑定服务对象解析异常！")
        }else {
            val fragment = SecondStepFragment.newInstance(json!!, phone, code)
            (activity as BindPhoneActivity).addFragment(fragment)
        }
    }

    private fun gotoLogin() {
        mPresenter.login(phone, code)//@date 2018-03-20 绑定成功 直接登陆
    }


}