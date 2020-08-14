package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.about

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.text.TextUtils
import android.widget.TextView
import kotlinx.android.synthetic.main.content_about.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.DownloadAPKFragment
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.service.DownloadAPKService
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.O2AppUpdateBean
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
    private fun checkAppUpdate(callbackContinue:((flag: Boolean)->Unit)? = null) {
        O2AppUpdateManager.instance().checkUpdate(this, object : O2AppUpdateCallback {
            override fun onUpdate(version: O2AppUpdateBean) {
                XLog.debug("onUpdateAvailable $version")
                versionName = version.versionName
                downloadUrl = version.downloadUrl
                XLog.info("versionName:$versionName, downloadUrl:$downloadUrl")
                O2DialogSupport.openConfirmDialog(this@AboutActivity,"版本 $versionName 更新："+version.content, listener = { _ ->
                    XLog.info("notification is true..........")
                    callbackContinue?.invoke(true)
                }, icon = O2AlertIconEnum.UPDATE, negativeListener = {_->
                    callbackContinue?.invoke(false)
                })
            }

            override fun onNoneUpdate(error: String) {
                XLog.info(error)
                XToast.toastShort(this@AboutActivity, "已经是最新版本了！")
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
