package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.about

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.widget.TextView
import com.pgyersdk.update.PgyUpdateManager
import com.pgyersdk.update.UpdateManagerListener
import kotlinx.android.synthetic.main.content_about.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2CustomStyle
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.DownloadAPKFragment
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.service.DownloadAPKService
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.PgyUpdateBean
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.dialog.O2AlertIconEnum
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.dialog.O2DialogSupport

class AboutActivity : AppCompatActivity() {

    private var downloadFragment: DownloadAPKFragment? = null
    private var versionName = ""
    private var downloadUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        // 沉浸式状态栏
        ImmersedStatusBarUtils.setImmersedStatusBar(this)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_snippet_top_bar)
        toolbar.title = ""
        setSupportActionBar(toolbar)
        val toolbarTitle = findViewById<TextView>(R.id.tv_snippet_top_title)
        toolbarTitle.text = title
        toolbar.setNavigationIcon(R.mipmap.ic_back_mtrl_white_alpha)
        toolbar.setNavigationOnClickListener { finish() }

        tv_about_version_name.text = getString(R.string.version).plus(AndroidUtils.getAppVersionName(this))
        val copyRight = getString(R.string.copy_right).plus(" ")
                .plus(DateHelper.nowByFormate("yyyy")).plus(" ")
                .plus(getString(R.string.app_name_about)).plus(" ")
                .plus(getString(R.string.reserved))
        tv_about_reserved.text = copyRight

        val path = O2CustomStyle.launchLogoImagePath(this)
        if (!TextUtils.isEmpty(path)) {
            BitmapUtil.setImageFromFile(path!!, image_about_logo)
        }

        relative_about_check_version.setOnClickListener {
            checkAppUpdate()
        }
    }

    override fun onPause() {
        super.onPause()
        PgyUpdateManager.unregister()
    }


    /**
     * 检查应用是否需要更新
     */

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
                    downloadFragment?.show(supportFragmentManager, DownloadAPKFragment.DOWNLOAD_FRAGMENT_TAG)
                    downloadServiceStart()
                }
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
            downloadFragment?.show(supportFragmentManager, DownloadAPKFragment.DOWNLOAD_FRAGMENT_TAG)
            downloadServiceStart()
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
                    val currentversionName = AndroidUtils.getAppVersionName(this@AboutActivity)
                    if (currentversionName != versionName) {
                        O2DialogSupport.openConfirmDialog(this@AboutActivity,"版本 $versionName 更新："+ bean.data.releaseNote, listener = { _ ->
                            XLog.info("notification is true..........")
                            callbackContinue?.invoke(true)
                        }, icon = O2AlertIconEnum.UPDATE, negativeListener = { _->
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
                XToast.toastShort(this@AboutActivity, "没有发现新版本！")
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
}
