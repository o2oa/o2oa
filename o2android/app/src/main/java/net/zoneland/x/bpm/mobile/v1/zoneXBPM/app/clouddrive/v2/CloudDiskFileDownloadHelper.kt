package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.clouddrive.v2

import android.app.Activity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.APIAddressHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.enums.APIDistributeTypeEnum
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.FileExtensionHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.O2FileDownloadHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import rx.Observer
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File


/**
 * 云盘文件下载工具类
 */
class CloudDiskFileDownloadHelper(val activity: Activity) {

    var showLoading: (()->Unit)? = null
    var hideLoading: (()->Unit)? = null

//    var downloader: Future<Unit>? = null
    var subscription: Subscription? = null

    /**
     * 开始下载文件
     */
    fun startDownload(fileId: String, extension: String, result: (file: File?)->Unit) {
        showLoading?.invoke()

        val path = FileExtensionHelper.getXBPMTempFolder()+ File.separator + fileId + "." +extension
        XLog.debug("file path $path")
        val downloadUrl = APIAddressHelper.instance()
                .getCommonDownloadUrl(APIDistributeTypeEnum.x_file_assemble_control, "jaxrs/attachment2/$fileId/download/stream")
        XLog.debug("下载 文件 url: $downloadUrl")

        subscription = O2FileDownloadHelper.download(downloadUrl, path)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Boolean>{
                    override fun onError(e: Throwable?) {
                        result(null)
                    }

                    override fun onNext(t: Boolean?) {
                        result(File(path))
                    }

                    override fun onCompleted() {
                        hideLoading?.invoke()
                    }

                })

//        downloader = activity.doAsync {
//            var downfile = true
//            val path = FileExtensionHelper.getXBPMTempFolder()+ File.separator + fileId + "." +extension
//            XLog.debug("file path $path")
//            val file = File(path)
//            if (!file.exists()) {
//                XLog.debug("file not exist, ${file.path}")
//                try {
//                    val call = RetrofitClient.instance().cloudFileControlApi()
//                            .downloadFile(fileId)
//                    val response = call.execute()
//                    val input  = DataInputStream(response.body()?.byteStream())
//                    val output = DataOutputStream(FileOutputStream(file))
//                    val buffer = ByteArray(4096)
//                    var count = 0
//                    do {
//                        count = input.read(buffer)
//                        if (count > 0) {
//                            output.write(buffer, 0, count)
//                        }
//                    } while (count > 0)
//                    output.close()
//                    input.close()
//                    downfile = true
//                }catch (e: Exception){
//                    XLog.error("download file fail", e)
//                    file.delete()
//                    downfile = false
//                }
//            }
//
//            uiThread {
//                XLog.debug("执行了。。。。uiThread。")
//                hideLoading?.invoke()
//                if (downfile) {
//                    result(fresult(file)ile)
//                }else {
//                    if (file.exists()){
//                        file.delete()
//                    }
//                    result(null)
//                }
//            }
//        }


    }

    /**
     * 关闭下载
     */
    fun closeDownload() {
//        if (downloader != null) {
//            hideLoading?.invoke()
//            downloader?.cancel(true)
//        }
        hideLoading?.invoke()
        subscription?.unsubscribe()
    }
}