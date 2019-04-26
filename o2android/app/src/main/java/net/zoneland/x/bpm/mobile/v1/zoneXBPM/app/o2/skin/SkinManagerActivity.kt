package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.skin

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import kotlinx.android.synthetic.main.activity_skin_manager.*
import net.muliba.changeskin.FancySkinManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.main.MainActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.ImmersedStatusBarUtils
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.go
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.goAndClearBefore
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.dialog.O2DialogSupport

class SkinManagerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_skin_manager)
        // 沉浸式状态栏
        ImmersedStatusBarUtils.setImmersedStatusBar(this)

        toolbar_snippet_top_bar.title = ""
        setSupportActionBar(toolbar_snippet_top_bar)
        tv_snippet_top_title.text = getString(R.string.skin_manager)
        toolbar_snippet_top_bar.setNavigationIcon(R.mipmap.ic_back_mtrl_white_alpha)
        toolbar_snippet_top_bar.setNavigationOnClickListener {
            finish()
        }

        button_skin_manager_red.setOnClickListener {

            changeSkinConfirm{
                FancySkinManager.instance().resetDefaultSkin()
                changeButton()
                goAndClearBefore<MainActivity>()
            }
        }
        button_skin_manager_blue.setOnClickListener {

            changeSkinConfirm{
                FancySkinManager.instance().changeSkinInner("blue")
                //發送廣播 通知皮膚切換 有些地方需要重新獲取資源 如IndexFragment裡面的mActionBarBackgroundDrawable
                changeButton()
                goAndClearBefore<MainActivity>()
            }

        }
        changeButton()
//
//        card_skin_manager_puppy2018.setOnClickListener {
//            go<SkinShowActivity>()
//        }
    }

    override fun onResume() {
        super.onResume()
        changeButton()
    }

    private fun changeSkinConfirm(callBack:()->Unit) {
        O2DialogSupport.openConfirmDialog(this, "确认要切换皮肤吗，切换会重启应用", { _->
            callBack.invoke()
        })
    }

    private fun changeButton() {
        val cPackage = FancySkinManager.instance().currentSkinPackageName()
        val cPath = FancySkinManager.instance().currentSkinPath()
        val cSuffix = FancySkinManager.instance().currentSkinSuffix()
        XLog.info("package:$cPackage, path:$cPath, suffix:$cSuffix")
        if (TextUtils.isEmpty(cPackage) && TextUtils.isEmpty(cPath) && TextUtils.isEmpty(cSuffix)) {
            button_skin_manager_red.background = FancySkinManager.instance().getDrawable(this, R.drawable.button_background_30dp_disable)
            button_skin_manager_red.text = getString(R.string.skin_manager_using)
            button_skin_manager_red.isEnabled = false
            button_skin_manager_blue.background = FancySkinManager.instance().getDrawable(this, R.drawable.button_background_30dp_skin_manager_blue)
            button_skin_manager_blue.text = getString(R.string.skin_manager_official_blue)
            button_skin_manager_blue.isEnabled = true
        } else if (cSuffix == "blue" && TextUtils.isEmpty(cPackage) && TextUtils.isEmpty(cPath)) {
            button_skin_manager_red.background = FancySkinManager.instance().getDrawable(this, R.drawable.button_background_30dp_skin_manager_red)
            button_skin_manager_red.text = getString(R.string.skin_manager_official_red)
            button_skin_manager_red.isEnabled = true
            button_skin_manager_blue.background = FancySkinManager.instance().getDrawable(this, R.drawable.button_background_30dp_disable)
            button_skin_manager_blue.text = getString(R.string.skin_manager_using)
            button_skin_manager_blue.isEnabled = false
        } else {
            button_skin_manager_red.background = FancySkinManager.instance().getDrawable(this, R.drawable.button_background_30dp_skin_manager_red)
            button_skin_manager_red.text = getString(R.string.skin_manager_official_red)
            button_skin_manager_red.isEnabled = true
            button_skin_manager_blue.background = FancySkinManager.instance().getDrawable(this, R.drawable.button_background_30dp_skin_manager_blue)
            button_skin_manager_blue.text = getString(R.string.skin_manager_official_blue)
            button_skin_manager_blue.isEnabled = true
        }
    }

}
