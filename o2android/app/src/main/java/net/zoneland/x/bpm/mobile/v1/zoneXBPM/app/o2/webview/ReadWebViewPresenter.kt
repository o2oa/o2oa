package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.webview

import android.text.TextUtils
import net.muliba.accounting.app.ExceptionHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2App
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.ResponseHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.IdData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.AttachmentInfo
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.ReadData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.ReadInfoData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.FileExtensionHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.SDCardHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import okhttp3.MediaType
import okhttp3.RequestBody
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream

class ReadWebViewPresenter : BasePresenterImpl<ReadWebViewContract.View>(), ReadWebViewContract.Presenter {

    override fun loadReadInfo(id: String) {
        getProcessAssembleSurfaceServiceAPI(mView?.getContext())?.let { service->
                    service.getReadInfo(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler<ReadInfoData> { info -> mView?.loadReadInfo(info) },
                            ExceptionHandler(mView?.getContext(), { e -> mView?.finishLoading() }))
        }
    }

    override fun setReadComplete(read: ReadData?) {
            if (read==null) {
                mView?.invalidateArgs()
                return
            }
            val body = RequestBody.create(MediaType.parse("text/json"), O2SDKManager.instance().gson.toJson(read))
            getProcessAssembleSurfaceServiceAPI(mView?.getContext())?.let { service->
                service.setReadComplete(read.id, body)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler<IdData>{id->mView?.setReadCompletedSuccess()},
                            ExceptionHandler(mView?.getContext(), { e -> mView?.finishLoading() }))
        }
    }

    override fun downloadAttachment(attachmentId: String, workId: String) {
            if (TextUtils.isEmpty(attachmentId) || TextUtils.isEmpty(workId)) {
                mView?.invalidateArgs()
                return
            }
            getProcessAssembleSurfaceServiceAPI(mView?.getContext())?.let { service->
            service
                    .getWorkAttachmentInfo(attachmentId, workId)
                    .subscribeOn(Schedulers.io())
                    .flatMap { response ->
                        var info: AttachmentInfo? = response.data
                        if (info != null) {
                            val path = FileExtensionHelper.getXBPMWORKAttachmentFileByName(info.name)
                            val file = File(path)
                            if (!file.exists()) { //下载
                                try {
                                    SDCardHelper.generateNewFile(path)
                                    val call = service.downloadWorkAttachment(attachmentId, workId)
                                    val response = call.execute()
                                    val headerDisposition = response.headers().get("Content-Disposition")
                                    XLog.debug("header disposition: $headerDisposition")
                                    val dataInput = DataInputStream(response.body()?.byteStream())
                                    val fileOut = DataOutputStream(FileOutputStream(file))
                                    val buffer = ByteArray(4096)
                                    var count = 0
                                    do {
                                        count = dataInput.read(buffer)
                                        if (count > 0) {
                                            fileOut.write(buffer, 0, count)
                                        }
                                    } while (count > 0)
                                    fileOut.close()
                                    dataInput.close()
                                } catch (e: Exception) {
                                    XLog.error("下载附件失败！", e)
                                    if (file != null && file.exists()) {
                                        file.delete()
                                    }
                                }
                            }
                            Observable.create(object : Observable.OnSubscribe<File> {
                                override fun call(t: Subscriber<in File>?) {
                                    val thisfile = File(path)
                                    if (file != null && file.exists()) {
                                        t?.onNext(thisfile)
                                    } else {
                                        t?.onError(Exception("附件下载异常，找不到文件！"))
                                    }
                                    t?.onCompleted()
                                }
                            })
                        } else {
                            Observable.create(object : Observable.OnSubscribe<File> {
                                override fun call(t: Subscriber<in File>?) {
                                    t?.onError(Exception("没有获取到附件信息，无法下载附件！"))
                                    t?.onCompleted()
                                }
                            })
                        }

                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ file -> mView?.downloadAttachment(file) }, { e ->
                       mView?.downloadFail( "下载附件失败，${e.message}")
                    })
        }
    }

    override fun downloadWorkCompletedAttachment(attachmentId: String, workCompletedId: String) {
            if (TextUtils.isEmpty(attachmentId) || TextUtils.isEmpty(workCompletedId)) {
                mView?.invalidateArgs()
                return
            }
            getProcessAssembleSurfaceServiceAPI(mView?.getContext())?.let { service->
            service
                    .getWorkCompletedAttachmentInfo(attachmentId, workCompletedId)
                    .subscribeOn(Schedulers.io())
                    .flatMap { response ->
                        var info: AttachmentInfo? = response.data
                        if (info != null) {
                            val path = FileExtensionHelper.getXBPMWORKAttachmentFileByName(info.name)
                            val file = File(path)
                            if (!file.exists()) { //下载
                                try {
                                    SDCardHelper.generateNewFile(path)
                                    val call = service.downloadWorkCompletedAttachment(attachmentId, workCompletedId)
                                    val response = call.execute()
                                    val headerDisposition = response.headers().get("Content-Disposition")
                                    XLog.debug("header disposition: $headerDisposition")
                                    val dataInput = DataInputStream(response.body()?.byteStream())
                                    val fileOut = DataOutputStream(FileOutputStream(file))
                                    val buffer = ByteArray(4096)
                                    var count = 0
                                    do {
                                        count = dataInput.read(buffer)
                                        if (count > 0) {
                                            fileOut.write(buffer, 0, count)
                                        }
                                    } while (count > 0)
                                    fileOut.close()
                                    dataInput.close()
                                } catch (e: Exception) {
                                    XLog.error("下载附件失败！", e)
                                    if (file != null && file.exists()) {
                                        file.delete()
                                    }
                                }
                            }
                            Observable.create(object : Observable.OnSubscribe<File> {
                                override fun call(t: Subscriber<in File>?) {
                                    val thisfile = File(path)
                                    if (file != null && file.exists()) {
                                        t?.onNext(thisfile)
                                    } else {
                                        t?.onError(Exception("附件下载异常，找不到文件！"))
                                    }
                                    t?.onCompleted()
                                }
                            })
                        } else {
                            Observable.create(object : Observable.OnSubscribe<File> {
                                override fun call(t: Subscriber<in File>?) {
                                    t?.onError(Exception("没有获取到附件信息，无法下载附件！"))
                                    t?.onCompleted()
                                }
                            })
                        }

                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ file -> mView?.downloadAttachment(file) }, { e ->
                        mView?.downloadFail("下载失败，${e.message}")
                    })
        }
    }
}
