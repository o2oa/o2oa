package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.bind

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.KeyEvent
import android.view.WindowManager
import cn.jpush.android.api.JPushInterface
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2App
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.custom.CustomStyleFragment
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.login.LoginActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.main.MainActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.goThenKill
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.replaceFragmentSafely


/**
 * Created by fancy on 2017/6/8.
 */

class BindPhoneActivity: AppCompatActivity(), BindPhoneContract.View , DialogInterface.OnDismissListener {

    var deviceId = ""
    private lateinit var installCustomStyleFragment: CustomStyleFragment
    private val mPresenter:BindPhonePresenter = BindPhonePresenter()



    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.XBPMTheme_NoActionBar)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)//去掉信息栏
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fluid_app)
        mPresenter.attachView(this)

        deviceId = O2SDKManager.instance().prefs().getString(O2.PRE_BIND_PHONE_TOKEN_KEY, "")//检查本地是否存在设备号
        if (TextUtils.isEmpty(deviceId)){
            val nowToken = JPushInterface.getRegistrationID(this)
            if (!TextUtils.isEmpty(nowToken)) {
                deviceId =  nowToken
            }
        }
        XLog.debug("推送服务的本机deviceId：$deviceId")
        if (supportFragmentManager.fragments == null || supportFragmentManager.fragments.isEmpty()) {
            addFragment(FirstStepFragment())
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            if (supportFragmentManager.backStackEntryCount == 1) {
                finish()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }
    override fun onDestroy() {
        super.onDestroy()
        mPresenter.detachView()
    }

    override fun onDismiss(dialog: DialogInterface?) {
        XLog.info("dismiss install custom style success！！！！！！！！！！！！")
        goFinish()
    }

    override fun getContext(): Context  = this

    override fun customStyle(isNeedUpdate: Boolean) {
        if (isNeedUpdate) {
            installCustomStyleFragment = CustomStyleFragment()
            installCustomStyleFragment.isCancelable = false
            installCustomStyleFragment.show(supportFragmentManager, CustomStyleFragment.TAG)
        }else {
            goFinish()
        }
    }


    fun addFragment(fragment: Fragment){
        replaceFragmentSafely(fragment, fragment.javaClass.simpleName, R.id.fragment_container, true, true)
    }

    fun removeFragment() {
        try {
            if (supportFragmentManager.backStackEntryCount > 1) {
                supportFragmentManager.popBackStack()
            }else {
                finish()
            }
        } catch (e: Exception) {
            XLog.error("pop fragment error", e)
        }
    }

    fun loadDeviceId(): String {
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = O2SDKManager.instance().prefs().getString(O2.PRE_BIND_PHONE_TOKEN_KEY, "")//检查本地是否存在设备号
            if (TextUtils.isEmpty(deviceId)){//远程获取
                val nowToken = JPushInterface.getRegistrationID(this)
                if (!TextUtils.isEmpty(nowToken)) {
                    deviceId =  nowToken
                }
            }
        }
        XLog.info("load deviceId : $deviceId")
        return deviceId
    }

    var goToMain = true
    var phone = ""
    fun startInstallCustomStyle(goToMain:Boolean, phone:String = "") {
        this.goToMain = goToMain
        this.phone = phone
        mPresenter.checkCustomStyle()
    }


    private fun goFinish() {
        if (goToMain) {
            goThenKill<MainActivity>()
            O2App.instance._JMLoginInner()
        } else {
            goThenKill<LoginActivity>(LoginActivity.startBundleData(phone))
        }
    }
}