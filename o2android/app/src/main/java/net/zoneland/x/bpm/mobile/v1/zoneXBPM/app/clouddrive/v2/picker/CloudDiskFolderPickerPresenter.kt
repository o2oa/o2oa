package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.clouddrive.v2.picker

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.CloudDiskItem
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


class CloudDiskFolderPickerPresenter : BasePresenterImpl<CloudDiskFolderPickerContract.View>(), CloudDiskFolderPickerContract.Presenter {

    override fun getItemList(parentId: String) {
        val service = getCloudFileControlService(mView?.getContext()) ?: return
        val listFolderObservable = if (parentId.isEmpty()) {
            service.listFolderTop()
        }else {
            service.listFolderByFolderId(parentId)
        }
        listFolderObservable.subscribeOn(Schedulers.io())
                .flatMap {
                    val list = ArrayList<CloudDiskItem>()
                    val folderList = it.data
                    if (folderList != null && folderList.isNotEmpty()) {
                        folderList.forEach { folder ->
                            list.add(folder.copyToVO2())
                        }
                    }
                    Observable.just(list)
                }
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