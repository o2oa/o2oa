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
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.TaskData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.WorkLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.FileExtensionHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.FileUtil
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.SDCardHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Action1
import rx.schedulers.Schedulers
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream

class TaskWebViewPresenter : BasePresenterImpl<TaskWebViewContract.View>(), TaskWebViewContract.Presenter {


    override fun save(workId: String, formData: String) {
        if (TextUtils.isEmpty(workId) || TextUtils.isEmpty(formData)) {
            mView?.invalidateArgs()
            XLog.error("arguments is null  workid:$workId， formData:$formData")
            mView?.finishLoading()
            return
        }
        val body = RequestBody.create(MediaType.parse("text/json"), formData)
        getProcessAssembleSurfaceServiceAPI(mView?.getContext())?.let { service->
            service.saveTaskForm(body, workId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler<IdData> { _ -> mView?.saveSuccess() },
                            ExceptionHandler(mView?.getContext()) { e ->
                                XLog.error("", e)
                                mView?.finishLoading() })
        }
    }

    override fun delete(workId: String) {
        if (TextUtils.isEmpty(workId)) {
            mView?.invalidateArgs()
            return
        }
        getProcessAssembleSurfaceServiceAPI(mView?.getContext())?.let { service->
            service.deleteWorkForm(workId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .o2Subscribe {
                        onNext { response->
                            XLog.info("删除工作，${response.data?.id}")
                            mView?.deleteSuccess()
                        }
                        onError { e, isNetworkError ->
                            XLog.error("删除work error, isNet:$isNetworkError", e)
                            mView?.deleteFail()
                        }
                    }
        }

    }

    override fun submit(data: TaskData?, workId: String, formData: String?) {
        if (data == null || TextUtils.isEmpty(workId) || TextUtils.isEmpty(formData)) {
            mView?.invalidateArgs()
            XLog.error("arguments is null  workid:$workId， formData:$formData")
            mView?.finishLoading()
            return
        }
        val json = O2SDKManager.instance().gson.toJson(data)
        XLog.debug("task:$json")
        val body = RequestBody.create(MediaType.parse("text/json"), formData)
        XLog.debug("formData:$formData")
        getProcessAssembleSurfaceServiceAPI(mView?.getContext())?.let { service->
            service.saveTaskForm(body, workId)
                    .subscribeOn(Schedulers.io())
                    .flatMap { _ ->
                        val taskBody = RequestBody.create(MediaType.parse("text/json"), json)
                        service.postTask(taskBody, data.id)
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler<List<WorkLog>> { list -> mView?.submitSuccess() },
                            ExceptionHandler(mView?.getContext()) { e ->
                                XLog.error("", e)
                                mView?.finishLoading() })
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
                            ExceptionHandler(mView?.getContext()) { e ->
                                XLog.error("", e)
                                mView?.finishLoading() })
        }
    }

    override fun retractWork(workId: String) {
        getProcessAssembleSurfaceServiceAPI(mView?.getContext())?.let { service ->
            service.retractWork(workId).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler<IdData> { id -> mView?.retractSuccess() },
                            ExceptionHandler(mView?.getContext()) { e ->
                                XLog.error("", e)
                                mView?.retractFail() })
        }
    }

    override fun uploadAttachment(attachmentFilePath: String, site: String, workId: String) {
        if (TextUtils.isEmpty(attachmentFilePath) || TextUtils.isEmpty(site) || TextUtils.isEmpty(workId)) {
            mView?.invalidateArgs()
            XLog.error("arguments is null  workid:$workId， site:$site, attachmentFilePath:$attachmentFilePath")
            mView?.finishLoading()
            return
        }
        val file = File(attachmentFilePath)
        val requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), file)
        val body = MultipartBody.Part.createFormData("file", file.name, requestBody)
        val siteBody = RequestBody.create(MediaType.parse("text/plain"), site)
        getProcessAssembleSurfaceServiceAPI(mView?.getContext())?.let { service->
            service.uploadAttachment(body, siteBody, workId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler<IdData> { id -> mView?.uploadAttachmentSuccess(id.id, site) },
                            ExceptionHandler(mView?.getContext()) { e ->
                                XLog.error("$e")
                                mView?.finishLoading() })
        }
    }

    override fun replaceAttachment(attachmentFilePath: String, site: String, attachmentId: String, workId: String) {
        if (TextUtils.isEmpty(attachmentFilePath) || TextUtils.isEmpty(site) || TextUtils.isEmpty(attachmentId) || TextUtils.isEmpty(workId)) {
            mView?.invalidateArgs()
            XLog.error("arguments is null att:$attachmentId, workid:$workId， site:$site, attachmentFilePath:$attachmentFilePath")
            mView?.finishLoading()
            return
        }
        val file = File(attachmentFilePath)
        val requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), file)
        val body = MultipartBody.Part.createFormData("file", file.name, requestBody)
        getProcessAssembleSurfaceServiceAPI(mView?.getContext())?.let { service->
            service.replaceAttachment(body, attachmentId, workId)
                    .subscribeOn(Schedulers.io())
                    .flatMap { response ->

                        Observable.create(object : Observable.OnSubscribe<String> {
                            override fun call(t: Subscriber<in String>?) {
                                try {
                                    val idData: IdData? = response.data
                                    if (idData == null || TextUtils.isEmpty(idData.id)) {
                                        t?.onError(Exception("没有返回附件id"))
                                    } else {
                                        val parentFolder = FileExtensionHelper.getXBPMWORKAttachmentFolder()
                                        val folder = File(parentFolder)
                                        if (folder.exists()) {
                                            folder.listFiles().filter { (it != null && it.exists() && it.isFile) }.map(File::delete)
                                        }
                                        t?.onNext(idData.id)
                                    }
                                } catch (e: Exception) {
                                    t?.onError(e)
                                }
                                t?.onCompleted()
                            }
                        })
                    }.observeOn(AndroidSchedulers.mainThread())
                    .subscribe(Action1<String> { id -> mView?.replaceAttachmentSuccess(id, site) },
                            ExceptionHandler(mView?.getContext()) { e ->
                                XLog.error("", e)
                                mView?.finishLoading() })
        }
    }

    override fun downloadAttachment(attachmentId: String, workId: String) {
        if (TextUtils.isEmpty(attachmentId) || TextUtils.isEmpty(workId)) {
            mView?.invalidateArgs()
            XLog.error("arguments is null att:$attachmentId, workid:$workId")
            mView?.finishLoading()
            return
        }
        getProcessAssembleSurfaceServiceAPI(mView?.getContext())?.let { service->
            service.getWorkAttachmentInfo(attachmentId, workId)
                    .subscribeOn(Schedulers.io())
                    .flatMap { response ->
                        val info: AttachmentInfo? = response.data
                        if (info != null) {
                            val path = FileExtensionHelper.getXBPMWORKAttachmentFileByName(info.name)
                            val file = File(path)
                            if (!file.exists()) { //下载
                                try {
                                    SDCardHelper.generateNewFile(path)
                                    val call = service.downloadWorkAttachment(attachmentId, workId)
                                    val downloadRes = call.execute()
                                    val headerDisposition = downloadRes.headers().get("Content-Disposition")
                                    XLog.debug("header disposition: $headerDisposition")
                                    val dataInput = DataInputStream(downloadRes.body()?.byteStream())
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
                                    if (file.exists()) {
                                        file.delete()
                                    }
                                }
                            }
                            Observable.create { t ->
                                val thisfile = File(path)
                                if (file.exists()) {
                                    t?.onNext(thisfile)
                                } else {
                                    t?.onError(Exception("附件下载异常，找不到文件！"))
                                }
                                t?.onCompleted()
                            }
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
                    .subscribe({ file -> mView?.downloadAttachmentSuccess(file) }, { e ->
                        mView?.downloadFail( "下载附件失败，${e.message}")
                    })
        }
    }


    override fun upload2FileStorage(filePath: String, referenceType: String, reference: String, scale: Int) {
        XLog.debug("上传图片，filePath:$filePath, referenceType:$referenceType, reference:$reference, scale:$scale")
        if (filePath.isEmpty() || reference.isEmpty() || referenceType.isEmpty()) {
            mView?.upload2FileStorageFail("传入参数不正确！")
            return
        }
        val file = File(filePath)
        if (!file.exists()) {
            mView?.upload2FileStorageFail("文件不存在！！！")
            return
        }
        val fileService = getFileAssembleControlService(mView?.getContext())
        if (fileService!=null) {
            val mediaType = FileUtil.getMIMEType(file)
            val requestBody = RequestBody.create(MediaType.parse(mediaType), file)
            val body = MultipartBody.Part.createFormData("file", file.name, requestBody)
            fileService.uploadFile2ReferenceZone(body, referenceType, reference, scale)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler<IdData> {
                        id -> mView?.upload2FileStorageSuccess(id.id)
                    },
                            ExceptionHandler(mView?.getContext()) { e ->
                                XLog.error("$e")
                                mView?.upload2FileStorageFail("文件上传异常") })
        }else {
            mView?.upload2FileStorageFail("文件模块接入异常！")
        }

    }



}
