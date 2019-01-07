package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.my

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_clip_avatar.*
import kotlinx.android.synthetic.main.snippet_appbarlayout_toolbar.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.screenHeight
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.screenWidth


/**
 * Created by fancy on 2017/6/12.
 * Copyright © 2017 O2. All rights reserved.
 */


class ClipAvatarActivity : AppCompatActivity() {

    companion object {
        val AVATAR_URL = "avatarUri"
        fun startWithBundle(url: Uri): Bundle {
            val bundle = Bundle()
            bundle.putParcelable(AVATAR_URL, url)
            return bundle
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clip_avatar)
        // 沉浸式状态栏
        ImmersedStatusBarUtils.setImmersedStatusBar(this)

        val uri: Uri? = intent.extras?.getParcelable(AVATAR_URL)
        if (uri == null) {
            XToast.toastShort(this, "没有获取到需要裁剪的图片！")
            finish()
        }else {
            val cr = this.contentResolver
            try {
                val bitmap = BitmapUtil.getFitSampleBitmap(cr.openInputStream(uri), screenWidth(), screenHeight())
                clip_avatar_layout.setSrc(bitmap)
            } catch (e: Exception) {
                XLog.error("", e)
                XToast.toastShort(this, "没有获取到需要裁剪的图片！")
                finish()
            }
            toolbar_snippet_top_bar.title = ""
            setSupportActionBar(toolbar_snippet_top_bar)
            tv_snippet_top_title.text = getString(R.string.title_activity_clip_avatar)
            toolbar_snippet_top_bar.setNavigationIcon(R.mipmap.ic_back_mtrl_white_alpha)
            toolbar_snippet_top_bar.setOnClickListener { finish() }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_clip_avatar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_clip_avatar_save -> {//保存头像
                XLog.debug("in click clip button!")
                val bitmap = clip_avatar_layout.clip()
                ClipImageTask().execute(bitmap)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    internal inner class ClipImageTask : AsyncTask<Bitmap, Void, String>() {

        override fun doInBackground(vararg params: Bitmap): String {
            XLog.debug("clip finish !")
            val avatarFilePath = FileExtensionHelper.generateAvatarFilePath()
            val generateNewFile = SDCardHelper.generateNewFile(avatarFilePath)
            if (generateNewFile) {
                XLog.debug("generateFile finish file is $avatarFilePath")
                val bitmap72 = BitmapUtil.zoomBitmap(params[0], 72, 72)//压缩图片至 72*72 头像图片不需要太大
                SDCardHelper.bitmapToPNGFile(bitmap72, avatarFilePath)
            }
            return avatarFilePath
        }


        override fun onPostExecute(avatarFilePath: String) {
            XLog.debug("turn finish go out!")
            val intent = Intent()
            intent.putExtra("clipAvatarFilePath", avatarFilePath)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

}