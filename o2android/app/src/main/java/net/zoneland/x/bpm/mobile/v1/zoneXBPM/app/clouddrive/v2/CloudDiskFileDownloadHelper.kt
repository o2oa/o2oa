package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.clouddrive.v2

import android.app.Activity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.RetrofitClient
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.FileExtensionHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Future


/**
 * 云盘文件下载工具类
 */
class CloudDiskFileDownloadHelper(val activity: Activity) {

    var showLoading: (()->Unit)? = null
    var hideLoading: (()->Unit)? = null

    var downloader: Future<Unit>? = null

    /**
     * 开始下载文件
     */
    fun startDownload(fileId: String, extension: String, result: (file: File?)->Unit) {
        showLoading?.invoke()
        downloader = activity.doAsync {
            var downfile = true
            val path = FileExtensionHelper.getXBPMTempFolder()+ File.separator + fileId + "." +extension
            XLog.debug("file path $path")
            val file = File(path)
            if (!file.exists()) {
                XLog.debug("file not exist, ${file.path}")
                try {
                    val call = RetrofitClient.instance().cloudFileControlApi()
                            .downloadFile(fileId)
                    val response = call.execute()
                    val input  = DataInputStream(response.body()?.byteStream())
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
                    downfile = true
                }catch (e: Exception){
                    XLog.error("download file fail", e)
                    file.delete()
                    downfile = false
                }
            }

            uiThread {
                XLog.debug("执行了。。。。uiThread。")
                hideLoading?.invoke()
                if (downfile) {
                    result(file)
                }else {
                    if (file.exists()){
                        file.delete()
                    }
                    result(null)
                }
            }
        }
    }

    /**
     * 关闭下载
     */
    fun closeDownload() {
        if (downloader != null) {
            hideLoading?.invoke()
            downloader?.cancel(true)
        }
    }
}