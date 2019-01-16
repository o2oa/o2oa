package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.notice

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.content_notice_setting.*
import net.muliba.changeskin.FancySkinManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2App
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.enums.PhoneManufacturersEnum
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.ImmersedStatusBarUtils
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.edit
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.go
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.gone

class NoticeSettingActivity : AppCompatActivity(), View.OnClickListener {

    var helpKey = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice_setting)
        // 沉浸式状态栏
        ImmersedStatusBarUtils.setImmersedStatusBar(this)




        val toolbar = findViewById<Toolbar>(R.id.toolbar_snippet_top_bar)
        toolbar.title = ""
        setSupportActionBar(toolbar)
        val toolbarTitle = findViewById<TextView>(R.id.tv_snippet_top_title)
        toolbarTitle.text = title
        toolbar.setNavigationIcon(R.mipmap.ic_back_mtrl_white_alpha)
        toolbar.setNavigationOnClickListener { finish() }

        image_btn_notice_setting_notice_receive.setOnClickListener(this)
        image_btn_notice_setting_open_sound.setOnClickListener(this)
        image_btn_notice_setting_open_vibrate.setOnClickListener(this)
        relative_setting_message_help_button.setOnClickListener(this)

        XLog.info("系统："+android.os.Build.MANUFACTURER)
        if (TextUtils.isEmpty(android.os.Build.MANUFACTURER)) {
            relative_setting_message_help_button.gone()
        }else {
            when(android.os.Build.MANUFACTURER.toUpperCase()) {
                PhoneManufacturersEnum.HUAWEI.key.toUpperCase() -> {
                    helpKey = PhoneManufacturersEnum.HUAWEI.key
                    tv_setting_message_help_label.text = PhoneManufacturersEnum.HUAWEI.cn + getString(R.string.notice_setting_message_receive_help)
                }
                PhoneManufacturersEnum.XIAOMI.key.toUpperCase() -> {
                    helpKey = PhoneManufacturersEnum.XIAOMI.key
                    tv_setting_message_help_label.text = PhoneManufacturersEnum.XIAOMI.cn + getString(R.string.notice_setting_message_receive_help)
                }
                else -> {
                    helpKey = ""
                    tv_setting_message_help_label.text = getString(R.string.notice_setting_message_receive_help)
                    relative_setting_message_help_button.gone()
                }
            }
        }


        refreshView()
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.image_btn_notice_setting_notice_receive -> {
                var isnotice =  O2SDKManager.instance().prefs().getBoolean(O2.SETTING_MESSAGE_NOTICE_KEY, true)
                O2SDKManager.instance().prefs().edit { putBoolean(O2.SETTING_MESSAGE_NOTICE_KEY, !isnotice) }
                refreshView()
            }
            R.id.image_btn_notice_setting_open_sound -> {
                var isSound =  O2SDKManager.instance().prefs().getBoolean(O2.SETTING_MESSAGE_NOTICE_SOUND_KEY, true)
                O2SDKManager.instance().prefs().edit { putBoolean(O2.SETTING_MESSAGE_NOTICE_SOUND_KEY, !isSound) }
                refreshView()
            }
            R.id.image_btn_notice_setting_open_vibrate -> {
                var isNoticeVibrate =  O2SDKManager.instance().prefs().getBoolean(O2.SETTING_MESSAGE_NOTICE_VIBRATE_KEY,true)
                O2SDKManager.instance().prefs().edit { putBoolean(O2.SETTING_MESSAGE_NOTICE_VIBRATE_KEY, !isNoticeVibrate) }
                refreshView()
            }
            R.id.relative_setting_message_help_button -> {
                go<NoticeSettingHelpActivity>(NoticeSettingHelpActivity.startNoticeSettingHelp(helpKey))
            }
        }
    }

    private fun refreshView() {

        val isNotice =  O2SDKManager.instance().prefs().getBoolean(O2.SETTING_MESSAGE_NOTICE_KEY,true )
        if (isNotice) {
            image_btn_notice_setting_notice_receive.setImageDrawable(FancySkinManager.instance().getDrawable(this, R.mipmap.icon_toggle_on_29dp))
            lly_notice_setting_other.visibility = View.VISIBLE
            val isNoticeSound =  O2SDKManager.instance().prefs().getBoolean(O2.SETTING_MESSAGE_NOTICE_SOUND_KEY, true)
            if (isNoticeSound) {
                image_btn_notice_setting_open_sound.setImageDrawable(FancySkinManager.instance().getDrawable(this, R.mipmap.icon_toggle_on_29dp))
            }else {
                image_btn_notice_setting_open_sound.setImageResource(R.mipmap.icon_toggle_off_29dp)
            }
            val isNoticeVibrate =  O2SDKManager.instance().prefs().getBoolean(O2.SETTING_MESSAGE_NOTICE_VIBRATE_KEY,true)
            if (isNoticeVibrate) {
                image_btn_notice_setting_open_vibrate.setImageDrawable(FancySkinManager.instance().getDrawable(this, R.mipmap.icon_toggle_on_29dp))
            }else {
                image_btn_notice_setting_open_vibrate.setImageResource(R.mipmap.icon_toggle_off_29dp)
            }
        }else {
            image_btn_notice_setting_notice_receive.setImageResource(R.mipmap.icon_toggle_off_29dp)
            lly_notice_setting_other.visibility = View.GONE
        }
    }
}
