package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.main

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2App
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.realm.RealmDataService
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.portal.PortalData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.persistence.MyAppListObject
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.AppItemOnlineVo
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by fancy on 2017/6/8.
 */

class MyAppPresenter : BasePresenterImpl<MyAppContract.View>(), MyAppContract.Presenter {

    val service: RealmDataService by lazy { RealmDataService() }

    override fun getAllAppList() {
        val result = ArrayList<MyAppListObject>()
        service.findAllNativeApp()
                ?.subscribeOn(Schedulers.io())
                ?.flatMap { list ->
                    XLog.info("native app list from realm database : ${list?.size}")
                    list.filter { app -> app.enable }.map {
                        val obj = MyAppListObject()
                        obj.appId = it.key
                        obj.appTitle = it.name
                        result.add(obj)
                    }
                    service.findAllPortalList()
                }
                ?.flatMap { list ->
                    XLog.info("portal list from realm database : ${list?.size}")
                    list.filter { portal -> portal.enable }.map {
                        val obj = MyAppListObject()
                        obj.appId = it.id
                        obj.appTitle = it.name
                        result.add(obj)
                    }
                    if (result.isEmpty()) {
                        val url = O2SDKManager.instance().prefs().getString(O2.PRE_CENTER_URL_KEY, "")
                        getApiService(mView?.getContext(), url)
                                ?.getCustomStyle()
                                ?.subscribeOn(Schedulers.immediate())
                                ?.flatMap { response ->
                                    val data = response.data
                                    XLog.info("customStyle:$data")
                                    if (data != null) {
                                        val portalList = data.portalList
                                        val nativeAppList = data.nativeAppList
                                        nativeAppList.filter { app -> app.enable }.map {
                                            val obj = MyAppListObject()
                                            obj.appId = it.key
                                            obj.appTitle = it.name
                                            result.add(obj)
                                        }
                                        portalList.filter { portal -> portal.enable }.map {
                                            val obj = MyAppListObject()
                                            obj.appId = it.id
                                            obj.appTitle = it.name
                                            result.add(obj)
                                        }

                                        storagePortalList(portalList)
                                        storageNativeList(nativeAppList)
                                    }
                                    Observable.just(true)
                                }

                    } else {
                        Observable.just(true)
                    }
                }
                ?.observeOn(AndroidSchedulers.mainThread())?.o2Subscribe {
                    onNext {
                        mView?.setAllAppList(result)
                    }
                    onError { e, _ ->
                        XLog.error("", e)
                        mView?.setAllAppList(result)
                    }
                }


    }

    override fun getMyAppList() {
        mView.let {
            service.findMyAppList()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ result -> mView?.setMyAppList(result as ArrayList<MyAppListObject>) },
                            { e -> XLog.error("", e) })
        }
    }

    override fun addAndDelMyAppList(delAppList: ArrayList<MyAppListObject>, addAppList: ArrayList<MyAppListObject>) {
        mView.let {
            service.deleteMyApp(delAppList)
                    .subscribeOn(Schedulers.io())
                    .flatMap { response ->
                        if (response) {
                            RealmDataService().saveMyApp(addAppList)
                        } else {
                            Observable.just(response)
                        }
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { response -> mView?.addAndDelMyAppList(response) }
        }
    }

    private fun storageNativeList(nativeAppList: List<AppItemOnlineVo>) {
        service.deleteALlNativeApp().subscribeOn(Schedulers.immediate()).subscribe {
            service.saveNativeList(nativeAppList).subscribe()
        }
    }

    private fun storagePortalList(portalList: List<PortalData>) {
        service.deleteAllPortal().subscribeOn(Schedulers.immediate()).subscribe {
            service.savePortalList(portalList).subscribe()
        }
    }
}