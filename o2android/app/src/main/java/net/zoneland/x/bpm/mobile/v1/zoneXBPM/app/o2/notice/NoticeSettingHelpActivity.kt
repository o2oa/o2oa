package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.notice

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_notice_setting_help.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.enums.PhoneManufacturersEnum
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.ImmersedStatusBarUtils
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog

class NoticeSettingHelpActivity : AppCompatActivity() {

    companion object {
        val HELP_KEY = "HELP_KEY"
        fun startNoticeSettingHelp(key:String): Bundle {
            val bundle = Bundle()
            bundle.putString(HELP_KEY, key)
            return bundle
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice_setting_help)
        // 沉浸式状态栏
        ImmersedStatusBarUtils.setImmersedStatusBar(this)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_snippet_top_bar)
        toolbar.title = ""
        setSupportActionBar(toolbar)
        val toolbarTitle = findViewById<TextView>(R.id.tv_snippet_top_title)
        toolbarTitle.text = getString(R.string.title_notice_setting_message_receive_help)
        toolbar.setNavigationIcon(R.mipmap.ic_back_mtrl_white_alpha)
        toolbar.setNavigationOnClickListener { finish() }

        var key = intent.extras?.getString(HELP_KEY) ?: ""
        XLog.info("help key : $key")
        var helpUrl = "file:///android_asset/html/huawei.html"
        if (TextUtils.isEmpty(key)) {
            XLog.error("没有传入需要打开的手机帮助页面的key")
            finish()
        }else {
            when(key.toUpperCase()) {
                PhoneManufacturersEnum.HUAWEI.key.toUpperCase() -> helpUrl = "file:///android_asset/html/huawei.html"
                PhoneManufacturersEnum.XIAOMI.key.toUpperCase() -> helpUrl = "file:///android_asset/html/xiaomi.html"
            }
            XLog.info("help load html: $helpUrl")
            web_view_notice_setting_help_content.loadUrl(helpUrl)
        }

    }
}
