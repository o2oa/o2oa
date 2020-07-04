package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import rx.Observable
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL


/**
 * Created by fancyLou on 2020-06-22.
 * Copyright © 2020 O2. All rights reserved.
 */

object O2FileDownloadHelper {

    fun download(downloadUrl: String, outputFilePath: String): Observable<Boolean> {
        XLog.debug("准备下载文件 网络下载url: $downloadUrl 本地路径: $outputFilePath")
        return Observable.create { subscriber ->
            val file = File(outputFilePath)
            if (file.exists()) {
                subscriber.onNext(true)
                subscriber.onCompleted()
            }else {
                try {
                    SDCardHelper.generateNewFile(outputFilePath)
                    val url = URL(downloadUrl)
                    val conn = url.openConnection() as HttpURLConnection
                    conn.setRequestProperty("Accept-Encoding", "identity")
                    val newCookie = "x-token:" + O2SDKManager.instance().zToken
                    conn.setRequestProperty("Cookie", newCookie)
                    conn.setRequestProperty("x-token", O2SDKManager.instance().zToken)
                    conn.connect()
                    val inputStream = conn.inputStream
                    var fileName = conn.getHeaderField("Content-Disposition")
                    if (fileName!=null) {
                        fileName = fileName.substringAfterLast("''")
                    }
                    XLog.debug("下载文件名称: $fileName")
                    val fos = FileOutputStream(file, true)
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
                    subscriber.onNext(true)
                    subscriber.onCompleted()
                }catch (e: Exception){
                    try {
                        if (file.exists()) {
                            file.delete()
                        }
                    } catch (e: Exception) {}
                    subscriber.onError(e)
                    subscriber.onCompleted()
                }
            }
        }
    }


}