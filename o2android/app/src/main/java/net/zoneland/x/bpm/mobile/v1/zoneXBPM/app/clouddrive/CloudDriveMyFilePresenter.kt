package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.clouddrive

import android.text.TextUtils
import net.muliba.accounting.app.ExceptionHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.ResponseHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.enums.AttachmentType
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.enums.FileOperateType
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.IdData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.yunpan.FileJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.yunpan.FolderJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.yunpan.YunpanJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.YunpanItem
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File

class CloudDriveMyFilePresenter : BasePresenterImpl<CloudDriveMyFileContract.View>(), CloudDriveMyFileContract.Presenter {

    override fun uploadFile2Folder(folderId: String?, upFile: File) {
            if (TextUtils.isEmpty(folderId)) {
                mView?.responseErrorMessage("没有传入需要上传到哪个文件夹！")
                return
            }
            val requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), upFile)
            val body = MultipartBody.Part.createFormData("file", upFile.name, requestBody)
            getFileAssembleControlService(mView?.getContext())?.let { service->
                service.uploadFile2Folder(body, folderId!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler<IdData> { _ -> mView?.refreshView("上传成功！") },
                            ExceptionHandler(mView?.getContext()) { e -> mView?.responseErrorMessage(e.message ?: "") })

        }
    }

    override fun uploadFile2Top(upFile: File) {
            val requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), upFile)
            val body = MultipartBody.Part.createFormData("file", upFile.name, requestBody)
            getFileAssembleControlService(mView?.getContext())?.let { service->
                service.uploadFile2Root(body)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler<IdData> { _ -> mView?.refreshView("上传成功！") },
                            ExceptionHandler(mView?.getContext()) { e -> mView?.responseErrorMessage(e.message ?: "") })
        }
    }

    override fun createFolder(params: HashMap<String, String>) {
        getFileAssembleControlService(mView?.getContext())?.let { service->
            service.createFolder(params)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler<IdData> { id -> mView?.refreshView("创建文件夹成功！") },
                            ExceptionHandler(mView?.getContext()) { e -> mView?.responseErrorMessage(e.message ?: "") })
        }
    }

    override fun shareOrSendFile(id: String, sendList: ArrayList<String>, type: FileOperateType) {
            var file: FileJson? = null
            fileList.filter { it.id == id }.map { file = it }
            if (file != null) {
                if (type == FileOperateType.SEND) {
                    file!!.editorList = sendList
                } else {
                    file!!.shareList = sendList
                }
                getFileAssembleControlService(mView?.getContext())?.let { service->
                    service.updateFile(file!!, id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(ResponseHandler<IdData> { id ->
                            if (type == FileOperateType.SEND) {
                                mView?.refreshView("文件发送成功！")
                            } else {
                                mView?.refreshView("文件分享成功！")
                            }
                        },
                                ExceptionHandler(mView?.getContext()) { e -> mView?.responseErrorMessage(e.message ?: "") })
            }

        }
    }

    override fun deleteFile(type: AttachmentType, id: String) {
        getFileAssembleControlService(mView?.getContext())?.let { service->
            if (type == AttachmentType.FILE) {
                service.deleteFile(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(ResponseHandler<IdData> { id -> mView?.refreshView("删除成功！") },
                                ExceptionHandler(mView?.getContext()) { e -> mView?.responseErrorMessage(e.message ?: "") })
            } else {
                service.deleteFolder(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(ResponseHandler<IdData> { id -> mView?.refreshView("删除成功！") },
                                ExceptionHandler(mView?.getContext()) { e -> mView?.responseErrorMessage(e.message ?: "") })
            }
        }
    }

    override fun reNameFolder(id: String, content: String) {
            var folder: FolderJson? = null
            folderList.filter { it.id == id }.map { folder = it }
            if (folder != null) {
                folder!!.name = content
                getFileAssembleControlService(mView?.getContext())?.let { service->
                    service.reNameFolder(id, folder!!)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(ResponseHandler<IdData> { id -> mView?.refreshView("更新成功！") },
                                ExceptionHandler(mView?.getContext()) { e -> mView?.responseErrorMessage(e.message ?: "") })
            }
        }
    }

    override fun reNameFile(id: String, content: String) {
            var file: FileJson? = null
            fileList.filter { it.id == id }.map { file = it }
            if (file != null) {
                file!!.name = content
                getFileAssembleControlService(mView?.getContext())?.let { service->
                    service.reNameFile(id, file!!)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(ResponseHandler<IdData> { id -> mView?.refreshView("更新成功！") },
                                ExceptionHandler(mView?.getContext()) { e -> mView?.responseErrorMessage(e.message ?: "") })
            }
        }
    }

    override fun loadFileList(id: String) {
        getFileAssembleControlService(mView?.getContext())?.let { service->

            if (TextUtils.isEmpty(id)) {// 顶层
                service.getFileTopList()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(ResponseHandler<YunpanJson> { json -> mView?.loadFileList(transferFileList(json)) },
                                ExceptionHandler(mView?.getContext()) { e ->
                                    folderList.clear()
                                    fileList.clear()
                                    mView?.responseErrorMessage(e.message ?: "")
                                })
            } else {
                service.getFileListByFolder(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(ResponseHandler<YunpanJson> { json -> mView?.loadFileList(transferFileList(json)) },
                                ExceptionHandler(mView?.getContext()) { e ->
                                    folderList.clear()
                                    fileList.clear()
                                    mView?.responseErrorMessage(e.message ?: "")
                                })
            }
        }
    }

    private fun transferFileList(json: YunpanJson): ArrayList<YunpanItem> {
        val itemList = ArrayList<YunpanItem>()
        folderList.clear()
        folderList.addAll(json.folderList)
        folderList.map {
            itemList.add(it.copyToVO())
        }
        fileList.clear()
        fileList.addAll(json.attachmentList)
        fileList.map {
            itemList.add(it.copyToVO())
        }
        return itemList
    }

    val folderList = ArrayList<FolderJson>()
    val fileList = ArrayList<FileJson>()
}
