package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.login

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import kotlinx.android.synthetic.main.activity_login.*
import net.muliba.changeskin.FancySkinManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.bind.BindPhoneActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.main.MainActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.AuthenticationInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.BitmapUtil
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.DateHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.biometric.BioConstants
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.biometric.BiometryManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.biometric.OnBiometryAuthCallback
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.goThenKill
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.gone
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.CountDownButtonHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.dialog.O2DialogSupport
import java.io.IOException


/**
 * Created by fancy o7/6/8.
 */

class LoginActivity: BaseMVPActivity<LoginContract.View, LoginContract.Presenter>(),
        LoginContract.View, View.OnClickListener {

    override var mPresenter: LoginContract.Presenter = LoginPresenter()

    override fun beforeSetContentView() {
        super.beforeSetContentView()
        setTheme(R.style.XBPMTheme_NoActionBar)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)//去掉信息栏
    }

    companion object {
        const val REQUEST_PHONE_KEY = "REQUEST_PHONE_KEY"
        fun startBundleData(phone:String): Bundle {
            val bundle = Bundle()
            bundle.putString(REQUEST_PHONE_KEY, phone)
            return bundle
        }
    }

    private val countDownHelper: CountDownButtonHelper by lazy {
        CountDownButtonHelper(button_login_phone_code,
                getString(R.string.login_button_code),
                60,
                1)
    }

    //翻转动画
//    private val scale0 by lazy {
//        ScaleAnimation(1f,0f,1f,1f,
//            Animation.RELATIVE_TO_PARENT,0.5f,
//                Animation.RELATIVE_TO_PARENT,0.5f)
//    }
//    private val scale1 by lazy {
//        ScaleAnimation(0f,1f,1f,1f,
//                Animation.RELATIVE_TO_PARENT,0.5f,
//                Animation.RELATIVE_TO_PARENT,0.5f)
//    }
    private var receivePhone = ""

    //播放声音
    private var mediaPlayer: MediaPlayer? = null
    private var playBeep: Boolean = false


    override fun afterSetContentView(savedInstanceState: Bundle?) {
        receivePhone = intent.extras?.getString(REQUEST_PHONE_KEY) ?: ""
        //是否开启了指纹识别登录
        checkBioAuthLogin()

        setDefaultLogo()
        login_edit_username_id.setText(receivePhone)
        tv_login_copyright.text = getString(R.string.copy_right).plus(" ")
                .plus(DateHelper.nowByFormate("yyyy")).plus(" ")
                .plus(getString(R.string.app_name_about)).plus(" ")
                .plus(getString(R.string.reserved))
        login_edit_username_id.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                image_login_icon_name.setImageDrawable(FancySkinManager.instance().getDrawable(this, R.mipmap.icon_user_focus))
                view_login_username_bottom.setBackgroundColor(FancySkinManager.instance().getColor(this, R.color.z_color_input_line_focus))
            }else {
                image_login_icon_name.setImageDrawable(FancySkinManager.instance().getDrawable(this, R.mipmap.icon_user_normal))
                view_login_username_bottom.setBackgroundColor(FancySkinManager.instance().getColor(this, R.color.z_color_input_line_blur))
            }
        }
        login_edit_password_id.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                image_login_icon_password.setImageDrawable(FancySkinManager.instance().getDrawable(this, R.mipmap.icon_verification_code_focus))
                view_login_password_bottom.setBackgroundColor(FancySkinManager.instance().getColor(this, R.color.z_color_input_line_focus))
            }else {
                image_login_icon_password.setImageDrawable(FancySkinManager.instance().getDrawable(this, R.mipmap.icon_verification_code_normal))
                view_login_password_bottom.setBackgroundColor(FancySkinManager.instance().getColor(this, R.color.z_color_input_line_blur))
            }
        }



        btn_login_submit.setOnClickListener(this)
        btn_bio_auth_login.setOnClickListener(this)
        tv_user_fallback_btn.setOnClickListener(this)
        tv_bioauth_btn.setOnClickListener(this)


        if (BuildConfig.InnerServer) {
            login_edit_password_id.setHint(R.string.activity_login_password)
            login_edit_password_id.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
            button_login_phone_code.gone()
            tv_rebind_btn.gone()
        }else {
            login_edit_password_id.setHint(R.string.login_code)
            login_edit_password_id.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_NORMAL
            button_login_phone_code.visible()
            button_login_phone_code.setOnClickListener(this)
            tv_rebind_btn.visible()
            tv_rebind_btn.setOnClickListener(this)
        }

    }


    override fun layoutResId(): Int = R.layout.activity_login


    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        event?.let {
            if (it.keyCode == KeyEvent.KEYCODE_ENTER && it.action == KeyEvent.ACTION_DOWN) {
                if (login_edit_password_id.hasFocus()) {
                    /*隐藏软键盘*/
                    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    if (inputMethodManager.isActive) {
                        inputMethodManager.hideSoftInputFromWindow(this@LoginActivity.currentFocus!!.windowToken, 0)
                    }
                    submitLogin()
                    return true
                }
            }
        }
        return super.dispatchKeyEvent(event)
    }

    override fun onResume() {
        super.onResume()

        playBeep = true
        val audioService = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (audioService.ringerMode != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false
        }
        initBeepSound()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownHelper.destroy()
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.tv_user_fallback_btn -> {
                userFallback()
            }
            R.id.tv_bioauth_btn -> {
                showBiometryAuthUI()
            }
            R.id.btn_bio_auth_login ->{
                bioAuthLogin()
            }
            R.id.btn_login_submit -> {
                submitLogin()
            }
            R.id.button_login_phone_code -> {
                getVerificationCode()
            }
            R.id.tv_rebind_btn -> {
                O2DialogSupport.openConfirmDialog(this@LoginActivity, "确定要重新绑定手机号码吗？", { _ ->
                    reBindService()
                })
            }
        }
    }


    override fun loginSuccess(data: AuthenticationInfoJson) {
        if (login_main_biometry.visibility == View.VISIBLE) {
            playBeepSound()
        }
        hideLoadingDialog()
        O2SDKManager.instance().setCurrentPersonData(data)
        O2App.instance._JMLoginInner()
        goThenKill<MainActivity>()
    }

    override fun loginFail() {
        XToast.toastShort(this,  "登录失败, 请检查您的验证码是否输入正确！")
        hideLoadingDialog()
    }

    override fun getCodeError() {
        XToast.toastShort(this, "获取手机验证码失败，请检查您输入的用户名是否正确！")
    }

    private fun submitLogin() {
        val credential = login_edit_username_id.text.toString()
        val code = login_edit_password_id.text.toString()
        if (TextUtils.isEmpty(credential)) {
            XToast.toastShort(this, "用户名或手机号码 不能为空！")
            return
        }
        if (TextUtils.isEmpty(code)) {
            val label = if(BuildConfig.InnerServer){
                getString(R.string.login_code)
            }else {
                getString(R.string.activity_login_password)
            }
            XToast.toastShort(this, "$label 不能为空！")
            return
        }
        showLoadingDialog()
        if (BuildConfig.InnerServer) {
            mPresenter.loginByPassword(credential, code)
        }else {
            mPresenter.login(credential, code)
        }
    }


    private fun getVerificationCode() {
        val credential = login_edit_username_id.text.toString()
        if (TextUtils.isEmpty(credential)) {
            XToast.toastShort(this, "请输入用户名！")
            return
        }
        // 发送验证码
        mPresenter.getVerificationCode(credential)
        countDownHelper.start()
        //焦点跳转到验证码上面
        login_edit_password_id.isFocusable = true
        login_edit_password_id.isFocusableInTouchMode = true
        login_edit_password_id.requestFocus()
        login_edit_password_id.requestFocusFromTouch()
    }

    private fun reBindService() {
        O2SDKManager.instance().clearBindUnit()
        goThenKill<BindPhoneActivity>()
    }


    /**
     * 是否开启了指纹识别登录
     */
    private fun checkBioAuthLogin() {
        val userId = O2SDKManager.instance().prefs().getString(BioConstants.O2_bio_auth_user_id_prefs_key, "") ?: ""
        if (userId.isNotEmpty()) {
            tv_bioauth_btn.visible()
            login_form_scroll_id.gone()
            login_main_biometry.visible()
        }else {
            login_form_scroll_id.visible()
            login_main_biometry.gone()
        }
    }

    /**
     * 其他方式登录
     */
    private fun userFallback() {
        login_form_scroll_id.visible()
        login_main_biometry.gone()
    }

    /**
     * 指纹识别登录
     */
    private fun showBiometryAuthUI() {
        login_form_scroll_id.gone()
        login_main_biometry.visible()
    }

    private val bioManager: BiometryManager by lazy { BiometryManager(this) }
    /**
     * 指纹识别登录
     */
    private fun bioAuthLogin() {
        if(!bioManager.isBiometricPromptEnable()){
            XToast.toastShort(this, "指纹识别模块未启用，请检查手机是否开启")
        }else {
            val userId = O2SDKManager.instance().prefs().getString(BioConstants.O2_bio_auth_user_id_prefs_key, "") ?: ""
            bioManager.authenticate(object : OnBiometryAuthCallback{
                override fun onUseFallBack() {
                    XLog.error("点击了《其他方式》按钮。。。。。")
                    userFallback()
                }

                override fun onSucceeded() {
                    showLoadingDialog()
                    mPresenter.ssoLogin(userId)
                }

                override fun onFailed() {
                    XLog.error("指纹识别验证失败了。。。。。")
                    //XToast.toastShort(this@LoginActivity, "验证失败")
                }

                override fun onError(code: Int, reason: String) {
                    XLog.error("指纹识别验证出错，code:$code , reason:$reason")
                    //XToast.toastShort(this@LoginActivity, "验证失败")
                }

                override fun onCancel() {
                    XLog.info("指纹识别取消了。。。。。")
                }

            })
        }
    }

    /**
     * 设置logo
     */
    private fun setDefaultLogo() {
        val path = O2CustomStyle.loginAvatarImagePath(this@LoginActivity)
        if (!TextUtils.isEmpty(path)) {
            BitmapUtil.setImageFromFile(path!!, image_login_logo)
        }
    }



    ///////////////play media////////////
    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private val beepListener = MediaPlayer.OnCompletionListener { mediaPlayer -> mediaPlayer.seekTo(0) }

    private fun initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            volumeControlStream = AudioManager.STREAM_MUSIC
            mediaPlayer = MediaPlayer()
            mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer?.setOnCompletionListener(beepListener)
            val file = resources.openRawResourceFd(
                    R.raw.beep)
            try {
                mediaPlayer?.setDataSource(file.fileDescriptor,
                        file.startOffset, file.length)
                file.close()
                mediaPlayer?.setVolume(0.90f, 0.90f)
                mediaPlayer?.prepare()
            } catch (e: IOException) {
                mediaPlayer = null
            }
        }
    }

    private fun playBeepSound() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer?.start()
        }
    }



}