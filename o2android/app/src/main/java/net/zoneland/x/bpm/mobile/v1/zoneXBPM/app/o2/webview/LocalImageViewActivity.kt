package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.webview

import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import kotlinx.android.synthetic.main.activity_local_image_view.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.BitmapUtil
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.ToastUtil
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import java.io.File

class LocalImageViewActivity : AppCompatActivity() {

    companion object {
        const val local_image_view_file_path_key = "local_image_view_file_path_key"
        const val local_image_view_title_key = "local_image_view_title_key"
        fun startBundle(filePath: String, title:String = "图片查看"): Bundle {
            val bundle = Bundle()
            bundle.putString(local_image_view_file_path_key, filePath)
            bundle.putString(local_image_view_title_key, title)
            return bundle
        }
    }

    lateinit var filePath: String
    lateinit var title: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_local_image_view)
        title = intent.extras.getString(local_image_view_title_key) ?: "图片查看"
        filePath = intent.extras.getString(local_image_view_file_path_key) ?: ""
        if (TextUtils.isEmpty(filePath)) {
            XToast.toastShort(this, "传入参数不正确！")
            finish()
            return
        }
        tv_local_view_title.text = title
        image_local_view.setImageBitmap(BitmapFactory.decodeFile(filePath))
        image_local_view_back_btn.setOnClickListener {
            finish()
        }
    }
}
