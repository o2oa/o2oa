package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.process

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.service.PictureLoaderService
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.ImmersedStatusBarUtils
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.replaceFragmentSafely

class StartProcessActivity : AppCompatActivity() {

    var toolbar: Toolbar? = null
    var toolbarTitle: TextView? = null
    var pictureLoaderService: PictureLoaderService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_process)
        // 沉浸式状态栏
        ImmersedStatusBarUtils.setImmersedStatusBar(this)
        toolbar = findViewById(R.id.toolbar_snippet_top_bar)
        toolbar?.title = ""
        setSupportActionBar(toolbar)
        toolbarTitle = findViewById(R.id.tv_snippet_top_title)
        toolbarTitle?.text = ""
        toolbar?.setNavigationIcon(R.mipmap.ic_back_mtrl_white_alpha)
        toolbar?.setNavigationOnClickListener { removeFragment() }

        if (supportFragmentManager.fragments == null || supportFragmentManager.fragments.isEmpty()) {
            addFragment(StartProcessStepOneFragment())
        }

    }

    override fun onResume() {
        super.onResume()
        pictureLoaderService = PictureLoaderService(this)
    }

    override fun onPause() {
        super.onPause()
        pictureLoaderService?.close()
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

    fun setToolBarTitle(title:String) {
        toolbarTitle?.text = title
    }

    fun loadProcessApplicationIcon(convertView:View, appId:String) {
        pictureLoaderService?.loadProcessAppIcon(convertView, appId)
    }

    fun addFragment(fragment: Fragment){
        replaceFragmentSafely(fragment, fragment.javaClass.simpleName, R.id.fragment_container_start_process, true, true)
    }

    fun removeFragment() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
        }else {
            finish()
        }
    }
}
