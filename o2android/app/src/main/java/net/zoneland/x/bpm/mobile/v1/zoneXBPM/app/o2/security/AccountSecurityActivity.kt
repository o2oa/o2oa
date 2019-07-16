package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.security


import android.os.Bundle
import kotlinx.android.synthetic.main.activity_account_security.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.bind.BindPhoneActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.my.MyInfoActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.biometric.BioConstants
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.biometric.BiometryManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.biometric.OnBiometryAuthCallback
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.dialog.O2DialogSupport


class AccountSecurityActivity : BaseMVPActivity<AccountSecurityContract.View, AccountSecurityContract.Presenter>(), AccountSecurityContract.View {
    override var mPresenter: AccountSecurityContract.Presenter = AccountSecurityPresenter()
    override fun layoutResId(): Int  = R.layout.activity_account_security
    override fun afterSetContentView(savedInstanceState: Bundle?) {
        setupToolBar(getString(R.string.title_activity_account_security), true)

        account_name_id.text = O2SDKManager.instance().cName
        account_change_mobile_label_id.text = O2SDKManager.instance().prefs().getString(O2.PRE_BIND_PHONE_KEY, "")

        if (BuildConfig.InnerServer) {
            account_change_mobile_id.inVisible()
        }else {
            account_change_mobile_id.visible()
            account_change_mobile_id.setOnClickListener { changeMobile() }
        }

        rl_account_security_name_btn.setOnClickListener {
            go<MyInfoActivity>()
        }

        val unitName = O2SDKManager.instance().prefs().getString(O2.PRE_BIND_UNIT_KEY, "")
        tv_account_security_unit_name.text = "当前绑定服务器：$unitName"

        initBiometryAuthView()


        rl_account_security_bind_device_btn.setOnClickListener {
            go<DeviceManagerActivity>()
        }

    }

    override fun logoutSuccess() {
        O2SDKManager.instance().logoutCleanCurrentPerson()
        O2SDKManager.instance().clearBindUnit()
        O2App.instance._JMLogout()

        goAndClearBefore<BindPhoneActivity>()
    }

    private val bioManager: BiometryManager by lazy { BiometryManager(this) }
    private fun initBiometryAuthView() {

        if (bioManager.isBiometricPromptEnable()) {
            image_btn_account_security_biometry_enable.setOnClickListener {
                bioManager.authenticate(object : OnBiometryAuthCallback{
                    override fun onUseFallBack() {
                        XLog.error("点击了《其他方式》按钮。。。。。")
                    }

                    override fun onSucceeded() {
                        XLog.debug("指纹识别验证成功")
                        val userId = O2SDKManager.instance().prefs().getString(BioConstants.O2_bio_auth_user_id_prefs_key, "") ?: ""
                        XToast.toastShort(this@AccountSecurityActivity, "验证成功")
                        if (userId.isNotEmpty()) {
                            setBioAuthResult("")
                        }else {
                            setBioAuthResult(O2SDKManager.instance().cId)
                        }
                    }

                    override fun onFailed() {
                        XLog.error("指纹识别验证失败了。。。。。")
                        //XToast.toastShort(this@AccountSecurityActivity, "验证失败")
                    }

                    override fun onError(code: Int, reason: String) {
                        XLog.error("指纹识别验证出错，code:$code , reason:$reason")
                        //XToast.toastShort(this@AccountSecurityActivity, "验证失败，$reason")
                    }

                    override fun onCancel() {
                        XLog.info("指纹识别取消了。。。。。")
                    }

                })
            }
            tv_account_security_biometry_name.text = "指纹识别登录"
            val userId = O2SDKManager.instance().prefs().getString(BioConstants.O2_bio_auth_user_id_prefs_key, "") ?: ""
            if (userId.isNotEmpty()) {
                image_btn_account_security_biometry_enable.setImageResource(R.mipmap.icon_toggle_on_29dp)
            }else {
                image_btn_account_security_biometry_enable.setImageResource(R.mipmap.icon_toggle_off_29dp)
            }
        }else {
            tv_account_security_biometry_name.text = "指纹识别登录不可用"
            image_btn_account_security_biometry_enable.setImageResource(R.mipmap.icon_toggle_off_29dp)
            image_btn_account_security_biometry_enable.setOnClickListener {
                XToast.toastShort(this, "指纹识别不可用或未启用！")
            }
        }
    }

    private fun setBioAuthResult(userId: String) {
        O2SDKManager.instance().prefs().edit{
            putString(BioConstants.O2_bio_auth_user_id_prefs_key, userId)
        }
        if (userId.isNotEmpty()) {
            image_btn_account_security_biometry_enable.setImageResource(R.mipmap.icon_toggle_on_29dp)
        }else {
            image_btn_account_security_biometry_enable.setImageResource(R.mipmap.icon_toggle_off_29dp)
        }

    }


    private fun changeMobile() {
        O2DialogSupport.openConfirmDialog(this, "确定要重新绑定手机号码吗,该操作会清空当前登录信息，需要重新登录？", {
            val deviceId = O2SDKManager.instance().prefs().getString(O2.PRE_BIND_PHONE_TOKEN_KEY, "")
            mPresenter.logout(deviceId)
        })
    }



}
