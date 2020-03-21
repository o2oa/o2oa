package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.cms.application

import android.text.TextUtils
import net.muliba.accounting.app.ExceptionHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.ResponseHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.cms.CMSCategoryInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.cms.CMSDocumentInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
import okhttp3.MediaType
import okhttp3.RequestBody
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*

/**
 * Created by fancyLou on 2019-07-03.
 * Copyright © 2019 O2. All rights reserved.
 */

class CMSPublishDocumentPresenter : BasePresenterImpl<CMSPublishDocumentContract.View>(), CMSPublishDocumentContract.Presenter {
    override fun findCurrentPersonIdentity() {
        getAssemblePersonalApi(mView?.getContext())
                ?.getCurrentPersonInfo()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.o2Subscribe {
                    onNext {
                        val person = it.data
                        if (person!=null) {
                            person.woIdentityList
                            mView?.currentPersonIdentities(person.woIdentityList)
                        }
                    }
                    onError { e, isNetworkError ->
                        XLog.error("查询身份错误, netErr: $isNetworkError", e)
                    }
                }
    }

    override fun newDocument(doc: CMSDocumentInfoJson) {
        val json = O2SDKManager.instance().gson.toJson(doc)
        val body = RequestBody.create(MediaType.parse("text/json"), json)
        getCMSAssembleControlService(mView?.getContext())
                ?.documentPost(body)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.o2Subscribe {
                    onNext {
                        val id = it.data
                        if (id!=null) {
                            mView?.newDocumentId(id.id)
                        }else {
                            mView?.newDocumentFail("没有返回文档Id！")
                        }
                    }
                    onError { e, isNetworkError ->
                        XLog.error("保存文档错误, netErr: $isNetworkError", e)
                        mView?.newDocumentFail(e?.message ?: "")
                    }
                }
    }

    override fun startProcess(title: String, identifyId: String, category: CMSCategoryInfoJson) {
        val processId = category.workflowFlag
        val categoryId = category.id
        val appId = category.appId
        if (TextUtils.isEmpty(identifyId) || TextUtils.isEmpty(processId)) {
            mView?.startProcessFail("传入参数为空，无法启动流程，identity:$identifyId,processId:$processId")
            return
        }
        val body = ProcessStartCmsBo()
        body.title = title
        body.identity = identifyId
        val data = PData()
        val cms = CmsDocument()
        cms.creatorIdentity = identifyId
        cms.title = title
        cms.categoryId = categoryId
        cms.appId = appId
        cms.isNewDocument = true
        cms.categoryAlias = category.categoryAlias
        cms.categoryName = category.categoryName
        cms.docStatus = "draft"
        cms.createTime = Date()
        data.cmsDocument = cms
        body.data = data

        getProcessAssembleSurfaceServiceAPI(mView?.getContext())?.let { service ->
            service.startProcessForCms(processId, body)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler<List<ProcessWorkData>> { list ->
                        try {
                            mView?.startProcessSuccess(list[0].taskList[0].work, title)
                        } catch (e: Exception) {
                            XLog.error("", e)
                            mView?.startProcessFail("返回数据异常！${e.message}")
                        }
                    }, ExceptionHandler(mView?.getContext()) { e ->
                        mView?.startProcessFail(e.message ?: "")
                    })
        }
    }

}