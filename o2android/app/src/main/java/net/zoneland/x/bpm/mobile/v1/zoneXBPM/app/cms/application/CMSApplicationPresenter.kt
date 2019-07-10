package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.cms.application

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.CmsFilter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
import okhttp3.MediaType
import okhttp3.RequestBody
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class CMSApplicationPresenter : BasePresenterImpl<CMSApplicationContract.View>(), CMSApplicationContract.Presenter {




    override fun findDocumentDraftWithCategory(categoryId: String) {
        val put = CmsFilter()
        val cateList = ArrayList<String>()
        cateList.add(categoryId)
        put.categoryIdList = cateList
        val personList = ArrayList<String>()
        personList.add(O2SDKManager.instance().distinguishedName)
        put.creatorList= personList
        val json = O2SDKManager.instance().gson.toJson(put)
        val body = RequestBody.create(MediaType.parse("text/json"), json)

        getCMSAssembleControlService(mView?.getContext())?.findDocumentDraftListWithCategory(body)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.o2Subscribe {
                    onNext {
                        if (it.data!=null) {
                            mView?.documentDraft(it.data)
                        }else{
                            mView?.documentDraft(ArrayList())
                        }
                    }
                    onError { e, isNetworkError ->
                        XLog.error("查询草稿列表错误, netErr: $isNetworkError", e)
                        mView?.documentDraft(ArrayList())
                    }
                }
    }


    override fun loadCanPublishCategory(appId: String) {

        val service = getCMSAssembleControlService(mView?.getContext())
        service
                ?.canPublishCategories(appId)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.o2Subscribe {
                    onNext {
                        val app = it.data
                        if (app!=null && app.wrapOutCategoryList.isNotEmpty()) {
                            mView?.canPublishCategories(app.wrapOutCategoryList)
                        }
                    }
                    onError { e, isNetworkError ->
                        XLog.error("查询发布列表出错, netErr: $isNetworkError", e)
                    }
                }
    }


}
