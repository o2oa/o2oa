package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.service

import android.app.IntentService
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.v4.app.NotificationCompat
import android.widget.RemoteViews
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.FileUtil
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import android.app.NotificationChannel
import android.os.Build
import android.support.v4.content.LocalBroadcastManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.BuildConfig


/**
 * Created by fancyLou on 22/03/2018.
 * Copyright © 2018 O2. All rights reserved.
 */


class DownloadAPKService : IntentService("DownloadAPKService") {

    companion object {
        val DOWNLOAD_SERVICE_ACTION = ".action.UPDATE"
        val VERSIN_NAME_EXTRA_NAME = "VERSIN_NAME_EXTRA_NAME"
        val DOWNLOAD_URL_EXTRA_NAME = "DOWNLOAD_URL_EXTRA_NAME"
        val DOWNLOAD_RECIVER_ACTION_KEY = "DOWNLOAD_RECIVER_ACTION_KEY"
        val DOWNLOAD_PROGRESS_KEY = "DOWNLOAD_PROGRESS_KEY"
    }

    private lateinit var downloadHandler: Handler
//    private val mNotificationManager: NotificationManager by lazy { getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
//    private var mRemoteViews: RemoteViews? = null
//    private var mNotification: Notification? = null

    override fun onHandleIntent(intent: Intent?) {
        val action = intent?.action
        XLog.info("service action $action..........")
        val applicationId = BuildConfig.APPLICATION_ID
        XLog.info("applicationId:$applicationId")
        val downloadAction = applicationId + DOWNLOAD_SERVICE_ACTION
        XLog.info("downloadAction:$downloadAction")
        if (downloadAction == action) {
            startDownload(intent)
        }
    }

    private fun startDownload(intent: Intent) {
        downloadHandler = Handler(Looper.getMainLooper()) { msg: Message? ->
            XLog.info("receive message type:${msg?.arg1}")
            when (msg?.arg1) {
                0 -> {//create notify
                    createNotify()
                }
                1 -> {//update notify
                    updateNotify(msg)
                }
                2 -> {//finish download
                    installAPK(msg)
                }
            }

            true
        }

        val message = downloadHandler.obtainMessage()
        message.arg1 = 0 //开始下载
        downloadHandler.sendMessage(message)

        val file: File? = downloadFile(intent)
        finishUpdateMessage(file)
    }


    private fun downloadFile(intent: Intent): File? {
        val versionName = intent.getStringExtra(VERSIN_NAME_EXTRA_NAME)
        val downloadUrl = intent.getStringExtra(DOWNLOAD_URL_EXTRA_NAME)
        val file = File(FileUtil.appExternalCacheDir(applicationContext)?.absolutePath + File.separator + versionName + ".apk")
        return try {
            if (!file.exists()) {
                val url = URL(downloadUrl)
                val conn = url.openConnection() as HttpURLConnection
                conn.setRequestProperty("Accept-Encoding", "identity")
                conn.connect()
                val length = conn.contentLength
                val inputStream = conn.inputStream
                val fos = FileOutputStream(file, true)
                var oldProgress = 0
                val buf = ByteArray(1024 * 8)
                var currentLength = 0
                while (true) {
                    val num = inputStream.read(buf)
                    currentLength += num
                    // 计算进度条位置
                    val progress = (currentLength / length.toFloat() * 100).toInt()
                    if (progress > oldProgress) {
                        updateMessage(progress)
                        oldProgress = progress
                    }
                    if (num <= 0) {
                        break
                    }
                    fos.write(buf, 0, num)
                    fos.flush()
                }
                fos.flush()
                fos.close()
                inputStream.close()
            }
            file
        } catch (e: Exception) {
            XLog.error("下载应用安装包失败", e)
            try {
                file.deleteOnExit()
            } catch (e: Exception) {
            }
            null
        }

    }

    private fun createNotify() {
        try {
//            val channelID = "o2_notify_channel_down"
//            // 8.0开始要先建一个channel
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                val channel = NotificationChannel(channelID, "下载通知", NotificationManager.IMPORTANCE_HIGH)
//                mNotificationManager.createNotificationChannel(channel)
//            }
//            val mBuilder = NotificationCompat.Builder(this, channelID)
//            mBuilder.setSmallIcon(R.mipmap.logo)
//            mBuilder.setTicker(getString(R.string.downloading_begin))
//            mRemoteViews = RemoteViews(packageName, R.layout.remote_notification_download)
//            mRemoteViews?.setProgressBar(R.id.progressBar_remote_notification_download, 100, 0, false)
//            mBuilder.setCustomContentView(mRemoteViews)
//            mNotification = mBuilder.build()
//            mNotification?.flags = Notification.FLAG_NO_CLEAR
//            mNotificationManager.notify(0, mNotification)


        } catch (e: Exception) {
            XLog.error("创建通知异常", e)
        }

    }

    private fun updateNotify(msg: Message) {
        try {
            val flag = msg.obj
            if (flag != null) {
                flag as Int
//                mRemoteViews?.setProgressBar(R.id.progressBar_remote_notification_download, 100, flag, false)
//                mRemoteViews?.setTextViewText(R.id.tv_remote_notification_download_note, "正在下载 $flag %")
//                mNotificationManager.notify(0, mNotification)
                val intent = Intent(DOWNLOAD_RECIVER_ACTION_KEY)
                intent.putExtra(DOWNLOAD_PROGRESS_KEY, flag)
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
                XLog.debug("send progress $flag")
            }
        } catch (e: Exception) {
            XLog.error("更新通知异常", e)
        }
    }


    private fun installAPK(msg: Message) {
        try {
            val file = msg.obj
            if (file != null) {
                file as File
//                mRemoteViews?.setProgressBar(R.id.progressBar_remote_notification_download, 100, 100, false)
//                mRemoteViews?.setTextViewText(R.id.tv_remote_notification_download_note, "下载完成！")
//                mNotification?.flags = Notification.FLAG_AUTO_CANCEL
                val uri = FileUtil.getUriFromFile(applicationContext, file)
                val intent = Intent(Intent.ACTION_VIEW)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                val type = FileUtil.getMIMEType(file)
                XLog.info("文件类型:$type")
                intent.setDataAndType(uri, type)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)//这个参数要添加， 不然关闭应用之后再点击通知栏会报错
//                mNotification?.contentIntent = PendingIntent.getActivity(this, 0, intent, 0)
//                mNotificationManager.notify(0, mNotification)
                startActivity(intent)
                XLog.info("已经开始安装。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。")
            } else {
//                mRemoteViews?.setTextViewText(R.id.tv_remote_notification_download_note, "下载失败！")
//                mNotification?.flags = Notification.FLAG_AUTO_CANCEL
//                mNotificationManager.notify(0, mNotification)
            }
        } catch (e: Exception) {
            XLog.error("安装更新异常", e)
        }
    }


    private fun updateMessage(progress: Int) {
        val message = downloadHandler.obtainMessage()
        message.arg1 = 1
        message.obj = progress
        downloadHandler.sendMessage(message)
    }

    private fun finishUpdateMessage(file: File?) {
        val message = downloadHandler.obtainMessage()
        message.arg1 = 2
        message.obj = file
        downloadHandler.sendMessage(message)
    }


}