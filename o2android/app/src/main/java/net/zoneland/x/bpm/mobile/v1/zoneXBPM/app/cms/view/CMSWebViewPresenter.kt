package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.cms.view

import android.text.TextUtils
import net.muliba.accounting.app.ExceptionHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.ResponseHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.RetrofitClient
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.IdData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.cms.CMSDocumentAttachmentJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.AttachmentItemVO
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.FileExtensionHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.FileUtil
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
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

class CMSWebViewPresenter : BasePresenterImpl<CMSWebViewContract.View>(), CMSWebViewContract.Presenter {


    override fun uploadAttachment(attachmentFilePath: String, site: String, docId: String) {
        if (TextUtils.isEmpty(attachmentFilePath) || TextUtils.isEmpty(site) || TextUtils.isEmpty(docId)) {
            XLog.error("arguments is null  workid:$docId， site:$site, attachmentFilePath:$attachmentFilePath")
            mView?.finishLoading()
            return
        }
        val file = File(attachmentFilePath)
        val requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), file)
        val body = MultipartBody.Part.createFormData("file", file.name, requestBody)
        val siteBody = RequestBody.create(MediaType.parse("text/plain"), site)
        getCMSAssembleControlService(mView?.getContext())
                ?.uploadAttachment(body, siteBody, docId)
                ?.subscribeOn(Schedulers.io())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe(ResponseHandler { id -> mView?.uploadAttachmentSuccess(id.id, site) },
                            ExceptionHandler(mView?.getContext()) { e ->
                                XLog.error("$e")
                                mView?.finishLoading() })

    }

    override fun replaceAttachment(attachmentFilePath: String, site: String, attachmentId: String, docId: String) {
        if (TextUtils.isEmpty(attachmentFilePath) || TextUtils.isEmpty(site) || TextUtils.isEmpty(attachmentId) || TextUtils.isEmpty(docId)) {
            XLog.error("arguments is null att:$attachmentId, workid:$docId， site:$site, attachmentFilePath:$attachmentFilePath")
            mView?.finishLoading()
            return
        }
        val file = File(attachmentFilePath)
        val requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), file)
        val body = MultipartBody.Part.createFormData("file", file.name, requestBody)

        getCMSAssembleControlService(mView?.getContext())
                ?.replaceAttachment(body, attachmentId, docId)
                    ?.subscribeOn(Schedulers.io())
                    ?.flatMap { response ->
                        Observable.create(Observable.OnSubscribe<String> { t ->
                            try {
                                val idData: IdData? = response.data
                                if (idData == null || TextUtils.isEmpty(idData.id)) {
                                    t?.onError(Exception("没有返回附件id"))
                                } else {
                                    val parentFolder = FileExtensionHelper.getXBPMCMSAttachFolder()
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
                        })
                    }?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe(Action1<String> { id -> mView?.replaceAttachmentSuccess(id, site) },
                            ExceptionHandler(mView?.getContext()) { e ->
                                XLog.error("", e)
                                mView?.finishLoading() })

    }

    override fun downloadAttachment(attachmentId: String, documentId: String) {
        if (TextUtils.isEmpty(attachmentId) || TextUtils.isEmpty(documentId)) {
            XLog.error("arguments is null att:$attachmentId, documentId:$documentId")
            mView?.finishLoading()
            return
        }
        val cmsService = getCMSAssembleControlService(mView?.getContext())
        if (cmsService != null) {
            cmsService.getDocumentAttachment(attachmentId, documentId).subscribeOn(Schedulers.io())
                    .flatMap { res->
                        val attachInfo = res.data
                        if (attachInfo != null) {
                            val filePath = FileExtensionHelper.getXBPMCMSAttachFolder() + File.separator + attachInfo.name
                            val file = File(filePath)
                            try {
                                if (!file.exists()) {
                                    val call = cmsService.downloadAttach(attachmentId)
                                    val response = call.execute()
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
                                }
                            } catch (e: Exception) {
                                XLog.error("下载附件异常", e)
                                if (file.exists()) {
                                    file.delete()
                                }
                            }
                            Observable.create { t ->
                                val thisfile = File(filePath)
                                if (file.exists()) {
                                    t?.onNext(thisfile)
                                } else {
                                    t?.onError(Exception("附件下载异常，找不到文件！"))
                                }
                                t?.onCompleted()
                            }
                        }else {
                            Observable.create(object : Observable.OnSubscribe<File> {
                                override fun call(t: Subscriber<in File>?) {
                                    t?.onError(Exception("没有获取到附件信息，无法下载附件！"))
                                    t?.onCompleted()
                                }
                            })
                        }
                    }.observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ file -> mView?.downloadAttachmentSuccess(file) }, { e ->
                        mView?.downloadAttachmentFail( "下载附件失败，${e.message}")
                    })
        }else {
            XLog.error("cms模块接入异常！")
            mView?.downloadAttachmentFail("cms模块接入异常！")
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
