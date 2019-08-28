package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.organization

import net.muliba.accounting.app.ExceptionHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.ApiResponse
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.identity.IdentityLevelForm
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.person.PersonListLikeForm
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.unit.UnitJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.NewContactFragmentVO
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.NewContactListVO
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Action0
import rx.functions.Action1
import rx.schedulers.Schedulers

class NewOrganizationPresenter : BasePresenterImpl<NewOrganizationContract.View>(), NewOrganizationContract.Presenter {

    override fun loadChildrenWithParent(unitParentId: String) {
        val personService = getAssemblePersonalApi(mView?.getContext())
        val expressService = getAssembleExpressApi(mView?.getContext())
            getOrganizationAssembleControlApi(mView?.getContext())?.let { service->
            if (unitParentId == NewOrganizationActivity.UNIT_TOP_PARENT_ID) {
//                service.unitListTop().flatMap { response ->
//                    val retList = ArrayList<NewContactListVO>()
//                    val list = response.data
//                    if (list != null && list.isNotEmpty()) {
//                        list.map { retList.add(it.copyToOrgVO()) }
//                    }
//                    Observable.just(retList)
//                }
                personService?.getCurrentPersonInfo()?.flatMap { response ->
                    val person = response.data
                    if (person != null) {
                        val identityList = person.woIdentityList
                        if (identityList.isNotEmpty()) {
                            val identity = identityList[0]
                            val form = IdentityLevelForm(identity = identity.distinguishedName, level = 1)
                            expressService?.unitByIdentityAndLevel(form)
                        } else {
                            Observable.just(ApiResponse())
                        }
                    } else {
                        Observable.just(ApiResponse())
                    }
                }?.flatMap { response ->
                    val topList = ArrayList<NewContactListVO>()
                    val unit = response.data
                    if (unit != null) {
                        val u = unit.copyToOrgVO() as NewContactListVO.Department
                        u.departmentCount = 1
                        topList.add(u)
                    }
                    Observable.just(topList)
                }?.subscribeOn(Schedulers.io())
                        ?.observeOn(AndroidSchedulers.mainThread())
                        ?.subscribe(Action1<ArrayList<NewContactListVO>> { list -> mView?.callbackResult(list) },
                                ExceptionHandler(mView?.getContext()) { e -> mView?.backError(e.message ?: "") })
            } else {
                val unitObservable = service.unitSubDirectList(unitParentId).map { response ->
                    val retList = ArrayList<NewContactListVO>()
                    val list = response.data
                    if (list != null && list.isNotEmpty()) {
                        list.map { retList.add(it.copyToOrgVO()) }
                    }
                    retList
                }
                val identityObservable = service.identityListWithUnit(unitParentId).map { response ->
                    val retList = ArrayList<NewContactListVO>()
                    val list = response.data
                    if (list != null && list.isNotEmpty()) {
                        list.map { retList.add(it.copyToOrgVO()) }
                    }
                    retList
                }

                Observable.zip(unitObservable, identityObservable) { t1, t2 ->
                    val retList = ArrayList<NewContactListVO>()
                    retList.addAll(t1)
                    retList.addAll(t2)
                    retList
                }.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(Action1<ArrayList<NewContactListVO>> { list -> mView?.callbackResult(list) },
                                ExceptionHandler(mView?.getContext()) { e -> mView?.backError(e.message ?: "") })

            }
        }
    }

    override fun searchPersonWithKey(result: String) {
            val form = PersonListLikeForm( result)
            val backResult = ArrayList<NewContactListVO>()
            getOrganizationAssembleControlApi(mView?.getContext())?.let { service ->
                service.personListLike(form)
                        .subscribeOn(Schedulers.io())
                        .map { response ->
                            val retList = ArrayList<NewContactListVO>()
                            val list = response.data
                            if (list != null && list.isNotEmpty()) {
                                list.map {
                                    retList.add(NewContactListVO.Identity(
                                            name = it.name,
                                            person = it.id,
                                            distinguishedName = it.distinguishedName
                                    ))
                                }
                            }
                            retList
                        }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(Action1<ArrayList<NewContactListVO>> { list -> backResult.addAll(list) },
                                ExceptionHandler(mView?.getContext()) { e -> mView?.backError(e.message ?: "") },
                                Action0 { mView?.callbackResult(backResult) })
            }
        }
}
