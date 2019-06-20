package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.launch

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import cn.jpush.android.api.JPushInterface
import kotlinx.android.synthetic.main.activity_launch.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.DownloadAPKFragment
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.bind.BindPhoneActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.login.LoginActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.main.MainActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.enums.LaunchState
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.receiver.NetworkConnectStatusReceiver
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.permission.PermissionRequester
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.dialog.O2AlertDialogBuilder
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.dialog.O2AlertIconEnum
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.dialog.O2DialogSupport
import org.jetbrains.anko.dip
import android.support.annotation.RequiresApi
import com.pgyersdk.update.PgyUpdateManager
import com.pgyersdk.update.UpdateManagerListener
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.service.DownloadAPKService
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.PgyUpdateBean
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.*


/**
 * Created by fancy on 2017/6/7.
 */

class LaunchActivity : BaseMVPActivity<LaunchContract.View, LaunchContract.Presenter>(), LaunchContract.View {

    private var pushToken = ""
    //介绍页
    val introductionArray = intArrayOf(R.mipmap.introduction1, R.mipmap.introduction2, R.mipmap.introduction3,
            R.mipmap.introduction4, R.mipmap.introduction5)
    private val indicatorList = ArrayList<ImageView>()

    private var mStyleUpdate = false
    private var mCheckNetwork:Boolean? = null

    private val networkReceiver by lazy {
        NetworkConnectStatusReceiver { isConnected ->
            Log.d("LaunchActivity", "网络连接情况变化，isConnected:$isConnected")
            if (isConnected && mCheckNetwork == false){
                checkAppUpdate()
            }
        }
    }

    override var mPresenter: LaunchContract.Presenter = LaunchPresenter()

    override fun layoutResId(): Int = R.layout.activity_launch

    override fun beforeSetContentView() {
        super.beforeSetContentView()
        setTheme(R.style.XBPMTheme_NoActionBar)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)//去掉信息栏
    }

    override fun afterSetContentView(savedInstanceState: Bundle?) {
        val deviceId = O2SDKManager.instance().prefs().getString(O2.PRE_BIND_PHONE_TOKEN_KEY, "")//检查本地是否存在设备号
        if (!TextUtils.isEmpty(deviceId)) {
            Log.d("LaunchActivity", "本地存在设备号：$deviceId")
            pushToken = deviceId
        }
        if (TextUtils.isEmpty(pushToken)) {
            val nowToken = JPushInterface.getRegistrationID(this)
            if (!TextUtils.isEmpty(nowToken)) {
                pushToken = nowToken
            }
            Log.d("LaunchActivity", "推送服务的本机deviceId：$pushToken")
        }
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter()
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(networkReceiver, filter)
        //init introduction
        if (showIntroductionView()) {
            initIntroductionUI()
        }else {
            frame_launch_introduction_content.gone()
            constraint_launch_main_content.visible()
            start()
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(networkReceiver)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    fun start() {
        tv_launch_status.text = getString(R.string.launch_start) //开始启动
        circleProgressBar_launch.visible()
        PermissionRequester(this)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .o2Subscribe {
                    onNext { (granted, shouldShowRequestPermissionRationale, deniedPermissions) ->
                        Log.d("LaunchActivity", "granted:$granted, show:$shouldShowRequestPermissionRationale, deniedList:$deniedPermissions")
                        if (!granted) {
                            O2DialogSupport.openAlertDialog(this@LaunchActivity, "非常抱歉，应用需要存储权限才能正常运行， 马上去设置", { permissionSetting() })
                        } else {
                            checkNetwork()
                        }
                    }
                    onError { e, _ ->
                        Log.e("LaunchActivity", "检查权限出错", e)
                    }
                }
    }

    /**
     * 检查网络状态
     */
    private fun checkNetwork(){
        val cManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cManager.activeNetworkInfo
        if (activeNetwork == null || !activeNetwork.isConnected) {
            mCheckNetwork = false
            tv_launch_status.text = getString(R.string.launch_network_is_not_connected) //暂停 等待网络
            XToast.toastShort(this, getString(R.string.launch_network_is_not_connected))
        }else{
            mCheckNetwork = true
            // 是否使用蒲公英 launch()
            checkAppUpdate()
        }
    }


    ////////////////////////////更新 start///////////////////////////////////

    /**
     * 检查应用是否需要更新
     */

    private var downloadFragment: DownloadAPKFragment? = null
    private var versionName = ""
    private var downloadUrl = ""

    private fun checkAppUpdate() {
        checkAppUpdate(callbackContinue = { result ->
            if (result) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !packageManager.canRequestPackageInstalls()) {// 8.0需要判断安装未知来源的权限
                    startInstallPermissionSettingActivity()
                }else { // 下载安装更新
                    if (downloadFragment == null) {
                        downloadFragment = DownloadAPKFragment()
                    }
                    downloadFragment?.isCancelable = false
                    if (downloadFragment?.isAdded == true) {
                    }else {
                        downloadFragment?.show(supportFragmentManager, DownloadAPKFragment.DOWNLOAD_FRAGMENT_TAG)
                        downloadServiceStart()
                    }

                }
            }else {
                launch()
            }
        })
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 10086) { //  下载安装更新
            if (downloadFragment == null) {
                downloadFragment = DownloadAPKFragment()
            }
            downloadFragment?.isCancelable = false
            if (downloadFragment?.isAdded == true) {
            }else {
                downloadFragment?.show(supportFragmentManager, DownloadAPKFragment.DOWNLOAD_FRAGMENT_TAG)
                downloadServiceStart()
            }

        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun startInstallPermissionSettingActivity() {
        //注意这个是8.0新API
        val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
        startActivityForResult(intent, 10086)
    }
    private fun checkAppUpdate(noUpdateIsNotify: Boolean = false, callbackContinue:((flag: Boolean)->Unit)? = null) {
        PgyUpdateManager.register(this, object : UpdateManagerListener() {
            override fun onUpdateAvailable(p0: String?) {
                XLog.debug("onUpdateAvailable $p0")
                val bean = O2SDKManager.instance().gson.fromJson(p0, PgyUpdateBean::class.java)
                versionName = bean.data.versionName
                downloadUrl = bean.data.downloadURL
                XLog.info("versionName:$versionName, downloadUrl:$downloadUrl")
                if (bean != null) {
                    val currentversionName = AndroidUtils.getAppVersionName(this@LaunchActivity)
                    if (currentversionName != versionName) {
                        O2DialogSupport.openConfirmDialog(this@LaunchActivity,"版本 $versionName 更新："+ bean.data.releaseNote, listener = { _ ->
                            XLog.info("notification is true..........")
                            callbackContinue?.invoke(true)
//                            toDownloadService(activity)
                        }, icon = O2AlertIconEnum.UPDATE, negativeListener = {_->
                            callbackContinue?.invoke(false)
                        })

                    } else {
                        callbackContinue?.invoke(false)
                        XLog.info("versionName is same , do not show dialog! versionName:$versionName ")
                    }
                }else {
                    callbackContinue?.invoke(false)
                }

            }

            override fun onNoUpdateAvailable() {
                XLog.info("没有发现新版本！")

                callbackContinue?.invoke(false)
            }
        })
    }
    private fun downloadServiceStart() {
        val intent = Intent(this, DownloadAPKService::class.java)
        intent.action = packageName + DownloadAPKService.DOWNLOAD_SERVICE_ACTION
        intent.putExtra(DownloadAPKService.VERSIN_NAME_EXTRA_NAME, versionName)
        intent.putExtra(DownloadAPKService.DOWNLOAD_URL_EXTRA_NAME, downloadUrl)
        startService(intent)
    }

    /////////////////////////////////////更新 end//////////////////////////////////////


    private fun launch() {
        XLog.info("一切的开始。。。。。。。。。。。。。。。。。。。。。。。")
        if (BuildConfig.InnerServer) {
            val json = resources.assets.open("server.json")
            if (json!= null) {
                val len = json.available()
                val buffer = ByteArray(len)
                json.read(buffer)
                O2SDKManager.instance().launchInner(String(buffer, Charsets.UTF_8), launchState)
            }else {
                XLog.error("没有获取到server.json")
//                XToast.toastShort(this, "缺少配置文件！")
                O2AlertDialogBuilder(this)
                        .icon(O2AlertIconEnum.ALERT)
                        .content("缺少配置文件，请联系App开发人员！")
                        .positive("关闭")
                        .onPositiveListener{ _ ->
                            finish()
                        }
                        .show()
            }
        }else {
            if (TextUtils.isEmpty(pushToken)) {
                // 延迟3秒 再获取一次token
                Handler().postDelayed({
                    XLog.debug("delay 3 second check bind device")
                    val nowToken = JPushInterface.getRegistrationID(this)
                    XLog.debug("device : $nowToken")
                    O2SDKManager.instance().launch(nowToken, launchState)
                }, 3000)
            } else {
                O2SDKManager.instance().launch(pushToken, launchState)
            }

        }
    }

    /**
     *  启动过程
     */
    private val launchState : (state: LaunchState)->Unit = { state->
        runOnUiThread {
            when (state) {
                LaunchState.ConnectO2Collect -> {
                    tv_launch_status.text = getString(R.string.launch_check_bind) //检查绑定
                }
                LaunchState.ConnectO2Server -> {
                    tv_launch_status.text = getString(R.string.launch_load_center) //连接中心服务器
                }
                LaunchState.CheckMobileConfig -> {
                    tv_launch_status.text = getString(R.string.launch_check_style) //检查配置
                }
                LaunchState.DownloadMobileConfig -> {
                    mStyleUpdate = true
                    tv_launch_status.text = getString(R.string.launch_download_style) //下载配置
                }
                LaunchState.AutoLogin -> {
                    if (mStyleUpdate) {
                        mPresenter.downloadConfig()
                    }
                    tv_launch_status.text = getString(R.string.launch_auto_login) //准备登录
                    val path = O2CustomStyle.launchLogoImagePath(this)
                    if (!TextUtils.isEmpty(path)) {
                        BitmapUtil.setImageFromFile(path!!, image_launch_logo)
                    }
                }
                LaunchState.NoBindError -> {
                    gotoBindLogin()
                }
                LaunchState.NoLoginError -> {
                    gotoLogin()
                }
                LaunchState.UnknownError -> {
                    if (!isFinishing) {
                        if (BuildConfig.InnerServer) {
                            O2AlertDialogBuilder(this)
                                    .icon(O2AlertIconEnum.ALERT)
                                    .content("未知异常！")
                                    .positive("关闭")
                                    .onPositiveListener { _ ->
                                        finish()
                                    }
                                    .show()
                        } else {
                            O2AlertDialogBuilder(this)
                                    .title(R.string.confirm)
                                    .icon(O2AlertIconEnum.ALERT)
                                    .content("未知异常！")
                                    .positive("重新绑定")
                                    .negative("关闭")
                                    .onPositiveListener { _ ->
                                        gotoBindLogin()
                                    }
                                    .onNegativeListener { _ ->
                                        finish()
                                    }
                                    .show()
                        }
                    }
                }
                LaunchState.Success -> {
                    gotoMain()
                }
            }
        }
    }



    private fun gotoBindLogin() {
        circleProgressBar_launch.gone()
        goThenKill<BindPhoneActivity>()
    }

    private fun gotoLogin() {
        circleProgressBar_launch.gone()
        goThenKill<LoginActivity>()
    }

    private fun gotoMain() {
        O2App.instance._JMLoginInner()//im
        circleProgressBar_launch.gone()
        if (mStyleUpdate) {
            goAndClearBefore<MainActivity>()
        } else {
            goThenKill<MainActivity>()
        }
    }

    /**
     * 跳转设置页面
     */
    private fun permissionSetting() {
        val pkName = packageName
        val packageURI = Uri.parse("package:$pkName")
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI)
        startActivity(intent)
        finish()
    }


    private fun showIntroductionView(): Boolean =
            O2SDKManager.instance().prefs().getBoolean(O2.PRE_LAUNCH_INTRODUCTION_KEY, true)


    private fun initIntroductionUI() {
        frame_launch_introduction_content.visible()
        constraint_launch_main_content.gone()
        //生成indicator
        introductionArray.map {
            val indicator = ImageView(this@LaunchActivity)
            indicator.setImageResource(R.mipmap.ic_launch_introduction_indicator_dark)
            val param = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            param.setMargins(dip(10), 0, dip(10), 0)
            indicator.layoutParams = param
            linear_launch_introduction_bottom_indicator.addView(indicator)
            indicatorList.add(indicator)
        }
        view_pager_launch_introduction_page.adapter = object : PagerAdapter() {

            override fun instantiateItem(container: ViewGroup?, position: Int): Any {
                var page = container?.findViewWithTag<ImageView>(introductionPageTag(position))
                if (page == null) {
                    page = ImageView(this@LaunchActivity)
                    page.setImageResource(introductionArray[position])
                    page.scaleType = ImageView.ScaleType.FIT_XY
                    page.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    page.tag = introductionPageTag(position)
                    (container as ViewPager).addView(page)
                }
                return page
            }

            override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
                if (`object` != null) {
                    `object` as ImageView
                    container?.removeView(`object`)
                }
            }

            override fun isViewFromObject(view: View?, `object`: Any?): Boolean = view == `object`

            override fun getCount(): Int = introductionArray.size
        }
        view_pager_launch_introduction_page.addOnPageChangeListener {
            onPageSelected { position ->
                if (position == indicatorList.size - 1) {
                    btn_launch_introduction_bottom_enter.visible()
                    linear_launch_introduction_bottom_indicator.gone()
                } else {
                    btn_launch_introduction_bottom_enter.gone()
                    linear_launch_introduction_bottom_indicator.visible()
                    indicatorList.map { it.setImageResource(R.mipmap.ic_launch_introduction_indicator_dark) }
                    indicatorList[position].setImageResource(R.mipmap.ic_launch_introduction_indicator_light)
                }

            }
        }
        view_pager_launch_introduction_page.currentItem = 0
        indicatorList[0].setImageResource(R.mipmap.ic_launch_introduction_indicator_light)

        btn_launch_introduction_bottom_enter.setOnClickListener {
            O2SDKManager.instance().prefs().edit {
                putBoolean(O2.PRE_LAUNCH_INTRODUCTION_KEY, false)
            }
            frame_launch_introduction_content.gone()
            constraint_launch_main_content.visible()
            start()
        }
    }

    private fun introductionPageTag(position: Int): String = "page_$position"

}