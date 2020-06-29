package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.webview

import android.app.Activity
import android.os.Looper
import android.text.TextUtils
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.ValueCallback
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.tbs.FileReaderActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.RetrofitClient
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.download.DownloadProgressHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.go
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * Created by fancyLou on 2018/8/22.
 * Copyright © 2018 O2. All rights reserved.
 */
class DownloadDocument(val context: Activity) {

    /**
     * 下线公文 并打开预览
     */
    fun downloadDocumentAndOpenIt(url: String, finishCallback: (() -> Unit)) {
//        val urlBase = url.substringBeforeLast("/")
//        val id = url.substringAfterLast("/")
//        XLog.info("文档名称： $id ， baseUrl: $urlBase")
        XLog.info("开始下载文档： $url")
        Observable.create<String> { subscriber ->
            var file: File? = null
            try {
                val downloadUrl = URL(url)
                val conn = downloadUrl.openConnection() as HttpURLConnection
                conn.setRequestProperty("Accept-Encoding", "identity")
                val newCookie = "x-token:" + O2SDKManager.instance().zToken
                conn.setRequestProperty("Cookie", newCookie)
                conn.setRequestProperty("x-token", O2SDKManager.instance().zToken)
                conn.connect()
                val inputStream = conn.inputStream
                var fileName = conn.getHeaderField("Content-Disposition")
                if (fileName != null) {
                    fileName = fileName.substringAfterLast("''")
                }
                XLog.debug("下载文件名称: $fileName")
                val path = FileExtensionHelper.getXBPMWORKAttachmentFileByName(fileName)
                XLog.debug("本地文件存储地址： $path")
                file = File(path)
                val fos = FileOutputStream(file)
                val buf = ByteArray(1024 * 8)
                var currentLength = 0
                while (true) {
                    val num = inputStream.read(buf)
                    currentLength += num
                    // 计算进度条位置
                    if (num <= 0) {
                        break
                    }
                    fos.write(buf, 0, num)
                    fos.flush()
                }
                XLog.debug("file length :$currentLength")
                fos.flush()
                fos.close()
                inputStream.close()
                subscriber.onNext(path)
                subscriber.onCompleted()
            } catch (e: Exception) {
                try {
                    if (file?.exists() == true) {
                        file.delete()
                    }
                } catch (e: Exception) {
                }
                subscriber.onError(e)
                subscriber.onCompleted()
            }
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .o2Subscribe {
                    onNext { result ->
                        XLog.info("下载文档：$result")
                        if (TextUtils.isEmpty(result)) {
                            openFileWithTBS(result, "")
                        } else {
                            XToast.toastShort(context, "下载文档失败！")
                        }
                        finishCallback()
                    }
                    onError { e, _ ->
                        XLog.error("下文档失败", e)
                        XToast.toastShort(context, "下载文档失败！")
                        finishCallback()
                    }
                }

//        RetrofitClient.instance().skinDownloadService("$urlBase/", object : DownloadProgressHandler() {
//            override fun onProgress(progress: Long, total: Long, done: Boolean) {
//                XLog.debug("$progress $total, $done")
//                XLog.debug("是否在主线程中运行" + (Looper.getMainLooper() == Looper.myLooper()).toString())
//                val myP = 100 * progress / total
//                XLog.debug(String.format("%d%% done\n", myP))
//                XLog.debug("done --->$done")
//
//            }
//        }).skinDownload(id)
//                .subscribeOn(Schedulers.io())
//                .flatMap { response ->
//                    var isDownFileSuccess = false
//                    try {
//                        val headers = response.headers()
//                        var fileName = headers.get("Content-Disposition")
//                        if (fileName!=null) {
//                            fileName = fileName.substringAfterLast("''")
//                        }
//                        XLog.debug("filename: $fileName")
//                        path = FileExtensionHelper.getXBPMWORKAttachmentFileByName(fileName)
//                        XLog.debug("path: $path")
//                        val file = File(path)
//                        if (!file.exists()) {
//                            SDCardHelper.generateNewFile(path)
//                        }
//                        val input = DataInputStream(response.body()?.byteStream())
//                        val output = DataOutputStream(FileOutputStream(file))
//                        val buffer = ByteArray(4096)
//                        var count = 0
//                        do {
//                            count = input.read(buffer)
//                            if (count > 0) {
//                                output.write(buffer, 0, count)
//                            }
//                        } while (count > 0)
//                        output.close()
//                        input.close()
//                        isDownFileSuccess = true
//                    } catch (e: Exception) {
//                        XLog.error("download file fail", e)
//                        isDownFileSuccess = false
//                    }
//                    Observable.just(isDownFileSuccess)
//                }
    }

    //......没有集成
    private fun openFileWithTBS(path: String?, fileName: String) = if (!TextUtils.isEmpty(path)) {
        context.go<FileReaderActivity>(FileReaderActivity.startBundle(path!!))
//        AndroidUtils.openFileWithDefaultApp(context, File(path))
    } else {
        XLog.error("文档本地地址没有。。。。。。。。。。。")
    }
}