package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2

import android.content.*
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.content.LocalBroadcastManager
import android.view.*
import com.race604.drawable.wave.WaveDrawable
import kotlinx.android.synthetic.main.fragment_download_progress.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.service.DownloadAPKService
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog

/**
 * Created by fancyLou on 2019/1/28.
 * Copyright © 2019 O2. All rights reserved.
 */


class DownloadAPKFragment : DialogFragment()  {

    companion object {
        val DOWNLOAD_FRAGMENT_TAG = "DOWNLOAD_FRAGMENT_TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(android.app.DialogFragment.STYLE_NO_FRAME, R.style.customStyleDialogStyle) //NO_FRAME就是dialog无边框，0指的是默认系统Theme
    }

    override fun onStart() {
        super.onStart()
        val window = dialog.window
        window?.setGravity(Gravity.CENTER)
        window?.setWindowAnimations(R.style.DialogEmptyAnimation)//取消过渡动画 , 使DialogSearch的出现更加平滑
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.fragment_download_progress, container, false)


    var wave : WaveDrawable? = null
    private val localBroadcastManager: LocalBroadcastManager by lazy {
        LocalBroadcastManager.getInstance(activity.applicationContext)
    }
    private var receiver : BroadcastReceiver? = null
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setOnKeyListener(DialogInterface.OnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                return@OnKeyListener true
            }
            false
        })
        val bit = BitmapFactory.decodeResource(activity.resources, R.mipmap.icon_down_arrow)
        val draw = BitmapDrawable(activity.resources, bit)
        wave = WaveDrawable(draw)
        image_logo_wave.setImageDrawable(wave)

        val intentfilter = IntentFilter()
        intentfilter.addAction(DownloadAPKService.DOWNLOAD_RECIVER_ACTION_KEY)
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (DownloadAPKService.DOWNLOAD_RECIVER_ACTION_KEY == intent?.action) {
                    val progress = intent.getIntExtra(DownloadAPKService.DOWNLOAD_PROGRESS_KEY, 0)
                    XLog.debug("这里是progress: $progress")
                    wave?.level = progress * 100
                    if (progress == 100) { //结束关闭
                        dismissAllowingStateLoss()
                    }
                }
            }

        }
        localBroadcastManager.registerReceiver(receiver, intentfilter)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (receiver!=null) {
            localBroadcastManager.unregisterReceiver(receiver)
        }
    }
}