package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import cn.jpush.android.api.JPushInterface
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.main.MainActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog


/**
 * Created by fancyLou on 2018/7/17.
 * Copyright © 2018 O2. All rights reserved.
 */


class JpushNoticeBroadReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val bundle = intent?.extras
//        XLog.info( "onReceive - " + intent?.action)
        when(intent?.action) {
            JPushInterface.ACTION_REGISTRATION_ID -> {
                val regId = bundle?.getString(JPushInterface.EXTRA_REGISTRATION_ID)
                Log.d("JpushNoticeReceiver","接收Registration Id : $regId")
            }
            JPushInterface.ACTION_NOTIFICATION_RECEIVED -> {
                XLog.info("收到了通知。。。。。。")
                val title = bundle?.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE)
                XLog.info("收到 通知标题：$title")
                val noticeId = bundle?.getInt(JPushInterface.EXTRA_NOTIFICATION_ID)
                XLog.info("收到 通知Id：$noticeId")
            }
            JPushInterface.ACTION_NOTIFICATION_OPENED -> {
                XLog.info("打开了通知。。。。。。。。。")
                val title = bundle?.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE)
                XLog.info("打开 通知标题：$title")
                val noticeId = bundle?.getInt(JPushInterface.EXTRA_NOTIFICATION_ID)
                XLog.info("打开 通知Id：$noticeId")
                // 进入主界面
                val i = Intent(context, MainActivity::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                context?.startActivity(i)
                XLog.info("打开了 Main 页面！。。。。")
            }
        }
    }

}