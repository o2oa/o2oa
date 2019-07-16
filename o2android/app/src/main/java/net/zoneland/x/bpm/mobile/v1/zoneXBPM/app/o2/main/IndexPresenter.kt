package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.main

import net.muliba.accounting.app.ExceptionHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2App
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.ResponseHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.enums.ApplicationEnum
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.realm.RealmDataService
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.HotPictureOutData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.persistence.MyAppListObject
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
import okhttp3.MediaType
import okhttp3.RequestBody
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by fancy on 2017/6/9.
 */

class IndexPresenter : BasePresenterImpl<IndexContract.View>(), IndexContract.Presenter {

    override fun loadTaskList(lastId: String) {
        getProcessAssembleSurfaceServiceAPI(mView?.getContext())?.let { service ->
            service.getTaskListByPage(lastId, O2.DEFAULT_PAGE_NUMBER)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler({ list -> mView?.loadTaskList(list) }),
                            ExceptionHandler(mView?.getContext(), { e -> mView?.loadTaskListFail() }))
        }
    }

    override fun loadNewsList(lastId: String) {
        val wrapIn = HashMap<String, List<String>>()
        val status = ArrayList<String>()
        status.add("published")
        wrapIn["statusList"] = status
        val json = O2SDKManager.instance().gson.toJson(wrapIn)
        val body = RequestBody.create(MediaType.parse("text/json"), json)
        getCMSAssembleControlService(mView?.getContext())?.let { service ->
            service.filterDocumentList(body, lastId, O2.DEFAULT_PAGE_NUMBER)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .o2Subscribe {
                        onNext { response->
                            if (response.data!=null) {
                                mView?.loadNewsList(response.data)
                            }else{
                                mView?.loadNewsListFail()
                            }
                        }
                        onError { e, isNetworkError ->
                            XLog.error("获取新闻出错，$isNetworkError", e)
                            mView?.loadNewsListFail()
                        }
                    }

        }
    }

    override fun loadHotPictureList() {
        val body = RequestBody.create(MediaType.parse("text/json"), "{}")
        getHotPicAssembleControlServiceApi(mView?.getContext())?.let { service ->
            service.findHotPictureList(1, O2.SETTING_HOT_PICTURE_DEFAULT_SHOW_NUMBER, body)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler<List<HotPictureOutData>>({ list -> mView?.loadHotPictureList(list) }),
                            ExceptionHandler(mView?.getContext(), { e -> mView?.loadHotPictureListFail() }))
        }
    }

    override fun getMyAppList() {
        RealmDataService().findMyAppList()
                .subscribeOn(Schedulers.io())
                .flatMap { result ->
                    XLog.debug("getmyApplist..........................${result.size}.")
                    val list = ArrayList<MyAppListObject>()
                    if (result.isEmpty()) {
                        ApplicationEnum.values().mapIndexed { index, applicationEnum ->
                            if (index < 4) {
                                val myObj = MyAppListObject()
                                myObj.appId = applicationEnum.key
                                myObj.appTitle = applicationEnum.appName
                                list.add(myObj)
                            }
                        }
                    } else {
                        list.addAll(result)
                    }
                    Observable.just(list)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result ->
                            XLog.debug("success.................${result.size}")
                            mView?.setMyAppList(result ) },
                        { e -> XLog.error("", e)
                            val list = ArrayList<MyAppListObject>()
                            ApplicationEnum.values().mapIndexed { index, applicationEnum ->
                                if (index < 4) {
                                    val myObj = MyAppListObject()
                                    myObj.appId = applicationEnum.key
                                    myObj.appTitle = applicationEnum.appName
                                    list.add(myObj)
                                }
                            }
                            mView?.setMyAppList(list)
                        }
                )
    }

}