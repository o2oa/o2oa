package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.cms.application

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.cms.CMSDocumentInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
import okhttp3.MediaType
import okhttp3.RequestBody
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

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
                            mView?.newDocumentFail()
                        }
                    }
                    onError { e, isNetworkError ->
                        XLog.error("保存文档错误, netErr: $isNetworkError", e)
                        mView?.newDocumentFail()
                    }
                }
    }

}