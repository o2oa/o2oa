package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import kotlinx.android.synthetic.main.activity_video_player.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.go

class VideoPlayerActivity : AppCompatActivity() {



    companion object {
        const val VIDEO_URL_KEY = "VIDEO_URL_KEY"
        const val VIDEO_TITLE_KEY = "VIDEO_TITLE_KEY"
        fun startPlay(activity: Activity, videoUrl: String, title: String = "") {
            val bundle = Bundle()
            bundle.putString(VIDEO_URL_KEY, videoUrl)
            bundle.putString(VIDEO_TITLE_KEY, title)
            activity?.go<VideoPlayerActivity>(bundle)
        }
    }

    private var orientationUtils: OrientationUtils? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        val url = intent.getStringExtra(VIDEO_URL_KEY)
        val title = intent.getStringExtra(VIDEO_TITLE_KEY) ?: ""
        if (url != null && url.isNotEmpty()) {
            init(url, title)
        }else {
            XToast.toastShort(this, "播放地址为空！")
            finish()
        }
    }


    private fun init(url: String, title: String) {

        video_player.setUp(url, true, title)

        //增加封面
//        val imageView = ImageView(this)
//        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
//        imageView.setImageResource(R.mipmap.my_app_top)
//        video_player.thumbImageView = imageView
        //增加title
        video_player.titleTextView.visibility = View.VISIBLE
        //设置返回键
        video_player.backButton.visibility = View.VISIBLE
        //设置旋转
        orientationUtils = OrientationUtils(this, video_player)
        //设置全屏按键功能,这是使用的是选择屏幕，而不是全屏
        video_player.fullscreenButton.setOnClickListener(View.OnClickListener { orientationUtils?.resolveByClick() })
        //是否可以滑动调整
        video_player.setIsTouchWiget(true)
        //设置返回按键功能
        video_player.backButton.setOnClickListener(View.OnClickListener { onBackPressed() })
        video_player.startPlayLogic()
    }

    override fun onResume() {
        super.onResume()
        video_player.onVideoResume()
    }

    override fun onPause() {
        super.onPause()
        video_player.onVideoPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        GSYVideoManager.releaseAllVideos()
        orientationUtils?.releaseListener()
    }

    override fun onBackPressed() {
        //先返回正常状态
        if (orientationUtils?.screenType == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            video_player.fullscreenButton.performClick()
            return
        }
        //释放所有
        video_player.setVideoAllCallBack(null)
        super.onBackPressed()

    }
}
