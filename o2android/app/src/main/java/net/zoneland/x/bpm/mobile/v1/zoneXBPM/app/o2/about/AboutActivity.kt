package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.about

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.widget.TextView
import com.pgyersdk.update.PgyUpdateManager
import kotlinx.android.synthetic.main.content_about.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2CustomStyle
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.*

class AboutActivity : AppCompatActivity() {

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
            AppUpdateUtil(this).checkAppUpdate(true)
        }
    }

    override fun onPause() {
        super.onPause()
        PgyUpdateManager.unregister()
    }
}
