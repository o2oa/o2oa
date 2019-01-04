package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.webview

import android.graphics.Bitmap
import android.text.TextUtils
import net.muliba.accounting.app.ExceptionHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2App
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.ResponseHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.TaskData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.FileExtensionHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.SDCardHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File

/**
 * Created by fancyLou on 2018/9/17.
 * Copyright © 2018 O2. All rights reserved.
 */

class TaskWorkSubmitDialogPresenter : BasePresenterImpl<TaskWorkSubmitDialogContract.View>(),
        TaskWorkSubmitDialogContract.Presenter {


    override fun submit(sign: Bitmap?, data: TaskData?, workId: String, formData: String?) {
        if (data == null || TextUtils.isEmpty(workId) || TextUtils.isEmpty(formData)) {
            mView?.submitCallback(false, null)
            XLog.error("arguments is null  workid:$workId， formData:$formData")
            return
        }

        val body = RequestBody.create(MediaType.parse("text/json"), formData)
        XLog.debug("formData:$formData")
        val service = getProcessAssembleSurfaceServiceAPI(mView?.getContext())
        if (service == null) {
            XLog.error("没有服务。。。。。。")
            return
        }
        if (sign == null) {
            service.saveTaskForm(body, workId)
                    .subscribeOn(Schedulers.io())
                    .flatMap { _ ->
                        val json = O2SDKManager.instance().gson.toJson(data)
                        XLog.debug("task:$json")
                        val taskBody = RequestBody.create(MediaType.parse("text/json"), json)
                        service.postTask(taskBody, data.id)
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler { _ ->
                        mView?.submitCallback(true, null)
                    },
                            ExceptionHandler(mView?.getContext()) { e ->
                                XLog.error("", e)
                                mView?.submitCallback(false, null)
                            })

        } else {
            // 有签名的 先上传签名
            try {
                val tempSignPath = FileExtensionHelper.generateSignTempFilePath()
                val result = SDCardHelper.bitmapToPNGFile(sign, tempSignPath)
                XLog.info("生成签名图片:$tempSignPath , result: $result")
                val file = File(tempSignPath)
                val requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), file)
                val fileBody = MultipartBody.Part.createFormData("file", file.name, requestBody)
                val site = "\$mediaOpinion"
                val siteBody = RequestBody.create(MediaType.parse("text/plain"), site)
                XLog.info("site:$site")
                service.uploadAttachment(fileBody, siteBody, workId)
                        .subscribeOn(Schedulers.io())
                        .flatMap { res1->
                            val id = res1.data.id
                            data.mediaOpinion = id // 附件id写入到意见内
                            service.saveTaskForm(body, workId)
                        }.flatMap { _ ->
                            val json = O2SDKManager.instance().gson.toJson(data)
                            XLog.debug("task:$json")
                            val taskBody = RequestBody.create(MediaType.parse("text/json"), json)
                            service.postTask(taskBody, data.id)
                        }
                        .observeOn(AndroidSchedulers.mainThread())
                        .o2Subscribe {
                            onNext {
                                mView?.submitCallback(true, site)
                            }
                            onError { e, isNetworkError ->
                                XLog.error("isNet:$isNetworkError", e)
                                mView?.submitCallback(false, null)
                            }
                        }
            } catch (e: Exception) {
                XLog.error("生成签名图片异常", e)
                mView?.submitCallback(false, null)
            }
        }
    }
}