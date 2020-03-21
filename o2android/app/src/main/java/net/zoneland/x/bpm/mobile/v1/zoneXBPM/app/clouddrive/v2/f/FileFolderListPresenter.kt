package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.clouddrive.v2.f

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.ApiResponse
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.IdData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.yunpan.CloudDiskShareForm
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.yunpan.FileJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.yunpan.FolderJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.CloudDiskItem
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File

class FileFolderListPresenter : BasePresenterImpl<FileFolderListContract.View>(), FileFolderListContract.Presenter {



    override fun move(files: List<CloudDiskItem.FileItem>, folders: List<CloudDiskItem.FolderItem>, destFolderId: String) {
        val service = getCloudFileControlService(mView?.getContext()) ?: return
        val all : ArrayList<Observable<ApiResponse<IdData>>> = ArrayList()
        if (files.isNotEmpty()) {
            all.addAll(
                    files.map {
                        val json = FileJson(it.id, it.createTime, it.updateTime, it.name, it.person,
                                "", it.fileName, it.extension, it.contentType, it.storageName, it.fileId,
                                it.storage, it.type, it.length, destFolderId, it.lastUpdateTime, it .lastUpdatePerson)
                        service.updateFile(json, it.id)
                    }
            )
        }
        if (folders.isNotEmpty()) {
            all.addAll(
                    folders.map {
                        val json = FolderJson(it.id, it.createTime, it.updateTime, it.name, it.person,
                                "", destFolderId, it.attachmentCount, it.size, it.folderCount, it.status, it.fileId)
                        service.updateFolder(it.id, json)
                    }
            )
        }
        Observable.merge(all).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .o2Subscribe {
                    onNext {
                        mView?.moveSuccess()
                    }
                    onError { e, _ ->
                        XLog.error("", e)
                        mView?.error(e?.message ?: "错误！")
                    }
                }

    }

    override fun share(ids: List<String>, users: List<String>, orgs: List<String>) {
        val service = getCloudFileControlService(mView?.getContext()) ?: return
        val all = ids.map {
            val form = CloudDiskShareForm()
            form.fileId = it
            form.shareOrgList = orgs
            form.shareUserList = users
            service.share(form)
        }
        Observable.merge(all).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .o2Subscribe {
                    onNext {
                        mView?.shareSuccess()
                    }
                    onError { e, _ ->
                        XLog.error("", e)
                        mView?.error(e?.message ?: "错误！")
                    }
                }

    }


    override fun deleteBatch(fileIds: List<String>, folderIds: List<String>) {
        val service = getCloudFileControlService(mView?.getContext()) ?: return
        val all : ArrayList<Observable<ApiResponse<IdData>>> = ArrayList()
        if (fileIds.isNotEmpty()) {
            val deletes = fileIds.map { service.deleteFile(it) }
            all.addAll(deletes)
        }
        if (folderIds.isNotEmpty()) {
            val deleteFolders = folderIds.map { service.deleteFolder(it) }
            all.addAll(deleteFolders)
        }
        Observable.merge(all).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .o2Subscribe {
                    onNext {
                        mView?.deleteSuccess()
                    }
                    onError { e, _ ->
                        XLog.error("", e)
                        mView?.error(e?.message ?: "错误！")
                    }
                }
    }


    override fun updateFolder(folder: FolderJson) {
        val service = getCloudFileControlService(mView?.getContext()) ?: return
        service.updateFolder(folder.id, folder).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .o2Subscribe {
                    onNext {
                        mView?.updateSuccess()
                    }
                    onError { e, _ ->
                        XLog.error("", e)
                        mView?.error(e?.message ?: "错误！")
                    }
                }
    }


    override fun updateFile(file: FileJson) {
        val service = getCloudFileControlService(mView?.getContext()) ?: return
        service.updateFile(file, file.id).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .o2Subscribe {
                    onNext {
                        mView?.updateSuccess()
                    }
                    onError { e, _ ->
                        XLog.error("", e)
                        mView?.error(e?.message ?: "错误！")
                    }
                }
    }


    override fun uploadFile(parentId: String, file: File) {
        val service = getCloudFileControlService(mView?.getContext()) ?: return
        var folderId = parentId
        if (parentId.isEmpty()) {
            folderId = O2.FIRST_PAGE_TAG
        }
        val requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), file)
        val body = MultipartBody.Part.createFormData("file", file.name, requestBody)
        service.uploadFile2Folder(body, folderId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .o2Subscribe {
                    onNext {
                        mView?.uploadSuccess()
                    }
                    onError { e, _ ->
                        XLog.error("", e)
                        mView?.error(e?.message ?: "错误！")
                    }
                }
    }

    override fun createFolder(params: HashMap<String, String>) {
        val service = getCloudFileControlService(mView?.getContext()) ?: return
        service.createFolder(params).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .o2Subscribe {
                    onNext {
                        mView?.createFolderSuccess()
                    }
                    onError { e, _ ->
                        XLog.error("", e)
                        mView?.error(e?.message ?: "错误！")
                    }
                }
    }


    override fun getItemList(parentId: String) {
        val service = getCloudFileControlService(mView?.getContext()) ?: return
        if (parentId.isEmpty()) {
            Observable
                    .zip(service.listFolderTop(), service.listFileTop()) { r1, r2 ->
                        val list = ArrayList<CloudDiskItem>()
                        val folderList = r1.data
                        val fileList = r2.data
                        if (folderList != null && folderList.isNotEmpty()) {
                            folderList.forEach {
                                list.add(it.copyToVO2())
                            }
                        }
                        if (fileList!=null && fileList.isNotEmpty()) {
                            fileList.forEach {
                                list.add(it.copyToVO2())
                            }
                        }
                        list
                    }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .o2Subscribe {
                        onNext {
                            mView?.itemList(it)
                        }
                        onError { e, _ ->
                            XLog.error("", e)
                            mView?.error(e?.message ?: "错误！")
                        }
                    }
        }else {
            Observable
                    .zip(service.listFolderByFolderId(parentId), service.listFileByFolderId(parentId)) { r1, r2 ->
                        val list = ArrayList<CloudDiskItem>()
                        val folderList = r1.data
                        val fileList = r2.data
                        if (folderList != null && folderList.isNotEmpty()) {
                            folderList.forEach {
                                list.add(it.copyToVO2())
                            }
                        }
                        if (fileList!=null && fileList.isNotEmpty()) {
                            fileList.forEach {
                                list.add(it.copyToVO2())
                            }
                        }
                        list
                    }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .o2Subscribe {
                        onNext {
                            mView?.itemList(it)
                        }
                        onError { e, _ ->
                            XLog.error("", e)
                            mView?.error(e?.message ?: "错误！")
                        }
                    }
        }

    }

}