package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.webview

import android.text.TextUtils
import net.muliba.accounting.app.ExceptionHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.ResponseHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.IdData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.AttachmentInfo
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.FileExtensionHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.SDCardHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream

class TaskCompletedWebViewPresenter : BasePresenterImpl<TaskCompletedWebViewContract.View>(), TaskCompletedWebViewContract.Presenter {



    override fun retractWork(workId: String) {
        getProcessAssembleSurfaceServiceAPI(mView?.getContext())?.let { service ->
            service.retractWork(workId).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler<IdData> { id -> mView?.retractSuccess() },
                            ExceptionHandler(mView?.getContext(), { e -> mView?.retractFail() }))
        }
    }

    override fun downloadAttachment(attachmentId: String, workId: String) {
        if (TextUtils.isEmpty(attachmentId) || TextUtils.isEmpty(workId)) {
            mView?.invalidateArgs()
            return
        }
        getProcessAssembleSurfaceServiceAPI(mView?.getContext())?.let { service ->
            service.getWorkAttachmentInfo(attachmentId, workId)
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
                                        try {
                                            file.delete()
                                        } catch (e: Exception) {
                                        }
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
                    .subscribe(
                            { file -> mView?.downloadAttachment(file) },
                            { e ->
                        mView?.downloadFail("下载附件失败，${e.message}")
                    })
        }
    }

    override fun downloadWorkCompletedAttachment(attachmentId: String, workCompletedId: String) {
        if (TextUtils.isEmpty(attachmentId) || TextUtils.isEmpty(workCompletedId)) {
            mView?.invalidateArgs()
            return
        }
        getProcessAssembleSurfaceServiceAPI(mView?.getContext())?.let { service ->
            service.getWorkCompletedAttachmentInfo(attachmentId, workCompletedId)
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
                                        try {
                                            file.delete()
                                        } catch (e: Exception) {
                                        }
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
                        mView?.downloadFail("下载附件失败，${e.message}")
                    })
        }
    }
}
