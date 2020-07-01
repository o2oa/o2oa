package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.im

import android.text.TextUtils
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.APIAddressHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.RetrofitClient
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.im.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.FileExtensionHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.FileUtil
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.O2FileDownloadHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class O2ChatPresenter : BasePresenterImpl<O2ChatContract.View>(), O2ChatContract.Presenter  {


    override fun getConversation(id: String) {
        val service = getMessageCommunicateService(mView?.getContext())
        service?.conversation(id)?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.o2Subscribe {
                    onNext {
                        if (it.data != null) {
                            mView?.conversationInfo(it.data)
                        }else {
                            mView?.conversationGetFail()
                        }
                    }
                    onError { e, _ ->
                        XLog.error("", e)
                        mView?.conversationGetFail()
                    }
                }
    }

    override fun updateConversationTitle(id: String, title: String) {
        if (id.isEmpty() || title.isEmpty()) {
            mView?.updateFail("参数不正确，无法修改")
            return
        }
        val service = getMessageCommunicateService(mView?.getContext())
        val form = IMConversationUpdateForm()
        form.id = id
        form.title = title
        service?.updateConversation(form)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.o2Subscribe {
                    onNext {
                        if (it.data != null) {
                            mView?.updateSuccess(it.data)
                        } else {
                            mView?.updateFail("修改失败！")
                        }
                    }
                    onError { e, _ ->
                        XLog.error("", e)
                        mView?.updateFail("修改失败！")
                    }
                }

    }

    override fun updateConversationPeople(id: String, users: ArrayList<String>) {
        if (id.isEmpty() || users.isEmpty()) {
            mView?.updateFail("参数不正确，无法修改")
            return
        }
        val service = getMessageCommunicateService(mView?.getContext())
        val form = IMConversationUpdateForm()
        form.id = id
        form.personList = users
        service?.updateConversation(form)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.o2Subscribe {
                    onNext {
                        if (it.data != null) {
                            mView?.updateSuccess(it.data)
                        } else {
                            mView?.updateFail("修改失败！")
                        }
                    }
                    onError { e, _ ->
                        XLog.error("", e)
                        mView?.updateFail("修改失败！")
                    }
                }
    }

    override fun sendIMMessage(msg: IMMessage) {
        val service = getMessageCommunicateService(mView?.getContext())
        //audio 和 image 需要先上传文件 然后发送消息
        val body = O2SDKManager.instance().gson.fromJson(msg.body, IMMessageBody::class.java)
        if (body.type == MessageType.audio.key || body.type == MessageType.image.key) {
            val file = File(body.fileTempPath)
            val mediaType = FileUtil.getMIMEType(file)
            val requestBody = RequestBody.create(MediaType.parse(mediaType), file)
            val part = MultipartBody.Part.createFormData("file", file.name, requestBody)
            service?.uploadFile(msg.conversationId, body.type!!, part)
                    ?.subscribeOn(Schedulers.io())
                    ?.flatMap { idData ->
                        val id = idData.data.id
                        val extension = idData.data.fileExtension
                        if (!TextUtils.isEmpty(id)) {//消息体中添加fileId 并清楚暂存的本地地址fileTempPath
                            body.fileId = id
                            body.fileExtension = extension
                            body.fileTempPath = null
                            msg.body = O2SDKManager.instance().gson.toJson(body)
                            service.sendMessage(msg)
                        }else {
                            throw Exception("上传附件失败")
                        }
                    }
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.o2Subscribe {
                        onNext {
                            val id = it.data.id
                            if (id != null) {
                                mView?.sendMessageSuccess(id)
                            }else {
                                mView?.sendFail(msg.id)
                            }
                        }
                        onError { e, _ ->
                            XLog.error("", e)
                            mView?.sendFail(msg.id)
                        }
                    }
        }else {
            service?.sendMessage(msg)?.subscribeOn(Schedulers.io())
                    ?.observeOn(AndroidSchedulers.mainThread())?.o2Subscribe {
                        onNext {
                            val id = it.data.id
                            if (id != null) {
                                mView?.sendMessageSuccess(id)
                            }else {
                                mView?.sendFail(msg.id)
                            }
                        }
                        onError { e, _ ->
                            XLog.error("", e)
                            mView?.sendFail(msg.id)
                        }
                    }
        }
    }

    override fun getMessage(page: Int, conversationId: String) {
        val service = getMessageCommunicateService(mView?.getContext())
        service?.messageByPage(page, O2.DEFAULT_PAGE_NUMBER, IMMessageForm(conversationId))
                ?.subscribeOn(Schedulers.io())
                ?.flatMap { res->
                    val list = res.data
                    val result = ArrayList<IMMessage>()
                    if (list != null && list.isNotEmpty()) {
                        result.addAll(list.sortedBy { it.createTime })
                    }
                    Observable.just(result)
                }
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.o2Subscribe {
                    onNext {
                        val list = it
                        if (list != null) {
                            mView?.backPageMessages(list)
                        }else {
                            mView?.backPageMessages(ArrayList())
                        }
                    }
                    onError { e, _ ->
                        XLog.error("", e)
                        mView?.backPageMessages(ArrayList())
                    }
                }
    }

    override fun readConversation(conversationId: String) {
        val service = getMessageCommunicateService(mView?.getContext())
        service?.let {
            it.readConversation(conversationId).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .o2Subscribe {
                        onNext {
                            XLog.debug("read success")
                        }
                        onError { e, _ ->
                            XLog.error("read error", e)
                        }
                    }
        }
    }

    override fun getFileFromNetOrLocal(position: Int, body: IMMessageBody) {
        if (TextUtils.isEmpty(body.fileId) && !TextUtils.isEmpty(body.fileTempPath)) {
            XLog.debug("本地文件。。。。。")
            mView?.localFile(body.fileTempPath!!, body.type!!,  position)
        }else if (!TextUtils.isEmpty(body.fileId)) {
            val fileId = body.fileId!!
            val path = FileExtensionHelper.getXBPMTempFolder()+ File.separator + fileId + "." +body.fileExtension
            val downloadUrl = APIAddressHelper.instance().getImFileDownloadUrl(fileId)
            O2FileDownloadHelper.download(downloadUrl, path)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .o2Subscribe {
                        onNext {
                            XLog.debug("返回下载地址:$it")
                            mView?.localFile(path, body.type!!, position)
                        }
                        onError { e, _ ->
                            XLog.error("", e)
                            mView?.downloadFileFail("获取文件异常, ${e?.message}")
                        }
                    }




        }
    }
}