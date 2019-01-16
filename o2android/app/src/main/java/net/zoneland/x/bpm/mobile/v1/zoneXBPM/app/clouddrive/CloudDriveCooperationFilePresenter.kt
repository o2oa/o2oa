package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.clouddrive

import net.muliba.accounting.app.ExceptionHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.ResponseHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.yunpan.CooperationFileJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.yunpan.CooperationFolderJson
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class CloudDriveCooperationFilePresenter : BasePresenterImpl<CloudDriveCooperationFileContract.View>(), CloudDriveCooperationFileContract.Presenter {

    override fun loadShareFileList(folderName: String) {
        getFileAssembleControlService(mView?.getContext())?.let { service->
            service.getShareFileList(folderName)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler<List<CooperationFileJson>> { list -> mView?.setFileList(list.map(CooperationFileJson::copyToVO)) },
                            ExceptionHandler(mView?.getContext()) { e -> mView?.onException(e.message ?: "") })
        }
    }

    override fun loadShareFolderList() {
        getFileAssembleControlService(mView?.getContext())?.let { service->
            service.getShareTopList()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler<List<CooperationFolderJson>> { list -> mView?.setFolderList(list.map(CooperationFolderJson::copyToVO)) },
                            ExceptionHandler(mView?.getContext()) { e -> mView?.onException(e.message ?: "") })

        }
    }

    override fun loadReceiveFileList(folderName: String) {
        getFileAssembleControlService(mView?.getContext())?.let { service->
                    service.getReceiveFileList(folderName)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler<List<CooperationFileJson>> { list -> mView?.setFileList(list.map(CooperationFileJson::copyToVO)) },
                            ExceptionHandler(mView?.getContext()) { e -> mView?.onException(e.message ?: "") })
        }
    }

    override fun loadReceiveFolderList() {
        getFileAssembleControlService(mView?.getContext())?.let { service->
            service.getReceiveTopList()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler<List<CooperationFolderJson>> { list -> mView?.setFolderList(list.map(CooperationFolderJson::copyToVO)) },
                            ExceptionHandler(mView?.getContext()) { e -> mView?.onException(e.message ?: "") })
        }
    }
}
