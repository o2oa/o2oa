package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.bbs.section

import android.text.TextUtils
import net.muliba.accounting.app.ExceptionHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.ResponseHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.realm.RealmDataService
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.bbs.SubjectInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.bbs.SubjectPublishPermissionCheckJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.BBSCollectionSectionVO
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
import okhttp3.MediaType
import okhttp3.RequestBody
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class BBSSectionPresenter : BasePresenterImpl<BBSSectionContract.View>(), BBSSectionContract.Presenter {

    override fun canPublishSubject(sectionId: String) {
            if (TextUtils.isEmpty(sectionId) ) {
                XLog.error("没有传入sectionId, 无法检查发帖权限")
                return
            }
            getBBSAssembleControlService(mView?.getContext())?.let {service->
                service.subjectPublishableInSection(sectionId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler<SubjectPublishPermissionCheckJson>{ (checkResult) ->mView?.publishPermission(checkResult)},
                            ExceptionHandler(mView?.getContext()){e-> XLog.error("", e)})
        }
    }

    override fun whetherTheSectionHasBeenCollected(sectionId: String) {
        mView?.let {
            RealmDataService().hasTheSectionCollected(sectionId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ result -> it.hasBeenCollected(result) }, { e ->
                        XLog.error("", e)
                        it.hasBeenCollected(false)
                    })
        }
    }

    override fun collectOrCancelCollectSection(sectionId: String, isCollected: Boolean) {
        getBBSAssembleControlService(mView?.getContext())?.let {service->
            if (isCollected) {
                val ids = ArrayList<String>(1)
                ids.add(sectionId)
                RealmDataService().deleteBBSCollections(ids)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ result ->
                            if (result) {
                                mView?.collectOrCancelCollectSectionResponse(true, "取消成功！")
                            } else {
                                mView?.collectOrCancelCollectSectionResponse(false, "取消收藏失败！")
                            }
                        }, { e ->
                            XLog.error("", e)
                            mView?.collectOrCancelCollectSectionResponse(false, "取消收藏失败！")
                        })
            } else {
               service.querySectionById(sectionId)
                        .subscribeOn(Schedulers.io())
                        .flatMap { response ->
                            val section = response.data
                            val time = System.currentTimeMillis()
                            RealmDataService().saveBBSCollection(BBSCollectionSectionVO(section.id, section.sectionName,
                                    section.icon, time))
                        }.observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ result ->
                            if (result) {
                                mView?.collectOrCancelCollectSectionResponse(true, "收藏成功！")
                            } else {
                                mView?.collectOrCancelCollectSectionResponse(false, "收藏失败！")
                            }
                        }, { e ->
                            XLog.error("", e)
                            mView?.collectOrCancelCollectSectionResponse(false, "收藏失败！")
                        })
            }
        }

    }

    override fun loadSubjectList(sectionId: String, pagerNumber: Int) {
            if (TextUtils.isEmpty(sectionId) ) {
                mView?.loadFail("没有传入板块ID, 无法查询！")
                return
            }
            val json= "{\"sectionId\":\"$sectionId\",\"withTopSubject\":false}"
            val body = RequestBody.create(MediaType.parse("text/json"), json)
            getBBSAssembleControlService(mView?.getContext())?.let {service->
            if (pagerNumber > 1) {
                service.subjectListByPage(pagerNumber, O2.DEFAULT_PAGE_NUMBER, body)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .o2Subscribe {
                            onNext { response->
                                mView?.loadSuccess(response.data)
                            }
                            onError { e, isNetworkError ->
                                XLog.error("", e)
                                mView?.loadFail("获取帖子列表失败")
                            }
                        }

            }else {//第一页添加置顶贴
                val retList = ArrayList<SubjectInfoJson>()
                service.topSubjectListBySectionId(sectionId)
                        .subscribeOn(Schedulers.io())
                        .flatMap { response ->
                            if (response.data!=null && !response.data.isEmpty()) {
                                retList.addAll(response.data)
                            }
                            service.subjectListByPage(pagerNumber, O2.DEFAULT_PAGE_NUMBER, body)
                        }.observeOn(AndroidSchedulers.mainThread())
                        .o2Subscribe {
                            onNext { data->
                                retList.addAll(data.data)
                                mView?.loadSuccess(retList)
                            }
                            onError { e, isNetworkError ->
                                XLog.error("", e)
                                mView?.loadFail("获取帖子列表失败")
                            }
                        }
            }
        }
    }
}
