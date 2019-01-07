package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.bind

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_fluid_login_unit.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2App
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPFragment
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.APIAddressHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.RetrofitClient
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.APIDistributeData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.AuthenticationInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.CollectUnitData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import org.jetbrains.anko.dip


/**
 * Created by fancy on 2017/6/8.
 */


class SecondStepFragment: BaseMVPFragment<SecondStepContract.View, SecondStepContract.Presenter>(), SecondStepContract.View, View.OnClickListener {

    companion object {
        val LOGIN_UNIT_JSON_KEY = "LOGIN_UNIT_JSON_KEY"
        val LOGIN_UNIT_PHONE_KEY = "LOGIN_UNIT_PHONE_KEY"
        val LOGIN_UNIT_CODE_KEY = "LOGIN_UNIT_CODE_KEY"

        fun newInstance(json:String, phone:String, code:String) :SecondStepFragment {
            val fragment =  SecondStepFragment()
            val bundle = Bundle()
            bundle.putString(LOGIN_UNIT_JSON_KEY, json)
            bundle.putString(LOGIN_UNIT_PHONE_KEY, phone)
            bundle.putString(LOGIN_UNIT_CODE_KEY, code)
            fragment.arguments = bundle
            return fragment
        }
    }

    override var mPresenter: SecondStepContract.Presenter = SecondStepPresenter()

    var json = ""
    var phone = ""
    var code = ""
    var unitList: List<CollectUnitData> = ArrayList()


    override fun initData() {
        super.initData()
        if (arguments!=null) {
            json = arguments.getString(LOGIN_UNIT_JSON_KEY, "")
            phone = arguments.getString(LOGIN_UNIT_PHONE_KEY, "")
            code = arguments.getString(LOGIN_UNIT_CODE_KEY, "")
        }
        if (TextUtils.isEmpty(json)) {
            XToast.toastShort(activity, "没有获得单位信息！")
            (activity as BindPhoneActivity).removeFragment()
            return
        }else {
            unitList = O2SDKManager.instance().gson.fromJson(json, object :TypeToken<List<CollectUnitData>>(){}.type)
        }

    }

    override fun layoutResId(): Int = R.layout.fragment_fluid_login_unit

    override fun initUI() {
        radio_group_login_unit_choose_unit.removeAllViews()
        val layoutInflater = LayoutInflater.from(activity)
        var marginTop = activity.dip( 10f)
        for ((index, unit) in unitList.withIndex()) {
            val radio: RadioButton = layoutInflater.inflate(R.layout.snippet_radio_button, null) as RadioButton
            radio.text = (unit.name)
            radio.id = 100 + index
            if (index == 0) {
                radio.isChecked = true
            }
            val params = RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT)
            params.setMargins(0, marginTop, 0, 0)
            radio_group_login_unit_choose_unit.addView(radio, params)
        }
        button_login_unit_next.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.button_login_unit_next -> {
                val radioId = radio_group_login_unit_choose_unit.checkedRadioButtonId
                val deviceId = (activity as BindPhoneActivity).loadDeviceId()
                XLog.debug("电话：$phone, radio id:$radioId, token:$deviceId")
                showLoadingDialog()
                //更新http协议
                val unitData = unitList[(radioId-100)]
                RetrofitClient.instance().setO2ServerHttpProtocol(unitData.httpProtocol)
                APIAddressHelper.instance().setHttpProtocol(unitData.httpProtocol)
                mPresenter.bindDevice(deviceId, phone, code, unitData)
            }
        }
    }

    override fun bindSuccess(distributeData: APIDistributeData) {
        APIAddressHelper.instance().setDistributeData(distributeData)
        mPresenter.login(phone, code)
    }

    override fun bindFail() {
        hideLoadingDialog()
        XToast.toastShort(activity, "绑定服务器失败！")
        (activity as BindPhoneActivity).removeFragment()
    }

    override fun noDeviceId() {
        hideLoadingDialog()
        XToast.toastShort(activity, "没有获取到您的设备号，请检查应用的权限设置！")
        (activity as BindPhoneActivity).removeFragment()
    }

    override fun loginSuccess(data: AuthenticationInfoJson) {
        O2SDKManager.instance().setCurrentPersonData(data)
        hideLoadingDialog()
//        activity.goThenKill<MainActivity>()
        (activity as BindPhoneActivity).startInstallCustomStyle(true)
    }

    override fun loginFail() {
        hideLoadingDialog()
        //自动登陆失败 跳转过去手动登陆
//        activity.goThenKill<LoginActivity>(LoginActivity.startBundleData(phone))
        (activity as BindPhoneActivity).startInstallCustomStyle(false, phone)
    }



}