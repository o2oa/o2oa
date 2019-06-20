package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.webview

import android.app.Activity
import android.os.Looper
import android.text.TextUtils
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.ValueCallback
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

/**
 * Created by fancyLou on 2018/8/22.
 * Copyright © 2018 O2. All rights reserved.
 */
class DownloadDocument(val context: Activity) {

    /**
     * 下线公文 并打开预览
     */
    fun downloadDocumentAndOpenIt(url: String, finishCallback: (()->Unit)) {
        val urlBase = url.substringBeforeLast("/")
        val id = url.substringAfterLast("/")
        XLog.info("文档名称： $id ， baseUrl: $urlBase")
        var path = ""

        RetrofitClient.instance().skinDownloadService("$urlBase/", object : DownloadProgressHandler() {
            override fun onProgress(progress: Long, total: Long, done: Boolean) {
                XLog.debug("$progress $total, $done")
                XLog.debug("是否在主线程中运行" + (Looper.getMainLooper() == Looper.myLooper()).toString())
                val myP = 100 * progress / total
                XLog.debug(String.format("%d%% done\n", myP))
                XLog.debug("done --->$done")

            }
        }).skinDownload(id)
                .subscribeOn(Schedulers.io())
                .flatMap { response ->
                    var isDownFileSuccess = false
                    try {
                        val headers = response.headers()
                        var fileName = headers.get("Content-Disposition")
                        if (fileName!=null) {
                            fileName = fileName.substringAfterLast("''")
                        }
                        XLog.debug("filename: $fileName")
                        path = FileExtensionHelper.getXBPMWORKAttachmentFileByName(fileName)
                        XLog.debug("path: $path")
                        val file = File(path)
                        if (!file.exists()) {
                            SDCardHelper.generateNewFile(path)
                        }
                        val input = DataInputStream(response.body()?.byteStream())
                        val output = DataOutputStream(FileOutputStream(file))
                        val buffer = ByteArray(4096)
                        var count = 0
                        do {
                            count = input.read(buffer)
                            if (count > 0) {
                                output.write(buffer, 0, count)
                            }
                        } while (count > 0)
                        output.close()
                        input.close()
                        isDownFileSuccess = true
                    } catch (e: Exception) {
                        XLog.error("download file fail", e)
                        isDownFileSuccess = false
                    }
                    Observable.just(isDownFileSuccess)
                }.observeOn(AndroidSchedulers.mainThread())
                .o2Subscribe {
                    onNext { result ->
                        XLog.info("下载文档：$result")
                        if (result) {
                            openFileWithTBS(path, "")
                        }else {
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
    }

    //......没有集成
    private fun openFileWithTBS(path: String?, fileName: String) = if (!TextUtils.isEmpty(path)) {
        context.go<FileReaderActivity>(FileReaderActivity.startBundle(path!!))
//        AndroidUtils.openFileWithDefaultApp(context, File(path))
    }else {
        XLog.error("文档本地地址没有。。。。。。。。。。。")
    }
}