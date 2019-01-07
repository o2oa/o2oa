package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.organization

import net.muliba.accounting.app.ExceptionHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.person.PersonListLikeForm
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.NewContactListVO
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Action0
import rx.functions.Action1
import rx.schedulers.Schedulers

class NewOrganizationPresenter : BasePresenterImpl<NewOrganizationContract.View>(), NewOrganizationContract.Presenter {

    override fun loadChildrenWithParent(unitParentId: String) {

            getOrganizationAssembleControlApi(mView?.getContext())?.let { service->
            if (unitParentId == NewOrganizationActivity.UNIT_TOP_PARENT_ID) {
                service.unitListTop().flatMap { response ->
                    val retList = ArrayList<NewContactListVO>()
                    val list = response.data
                    if (list != null && !list.isEmpty()) {
                        list.map { retList.add(it.copyToOrgVO()) }
                    }
                    Observable.just(retList)
                }.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(Action1<ArrayList<NewContactListVO>> { list -> mView?.callbackResult(list) },
                                ExceptionHandler(mView?.getContext()) { e -> mView?.backError(e.message ?: "") })
            } else {
                val unitObservable = service.unitSubDirectList(unitParentId).map { response ->
                    val retList = ArrayList<NewContactListVO>()
                    val list = response.data
                    if (list != null && !list.isEmpty()) {
                        list.map { retList.add(it.copyToOrgVO()) }
                    }
                    retList
                }
                val identityObservable = service.identityListWithUnit(unitParentId).map { response ->
                    val retList = ArrayList<NewContactListVO>()
                    val list = response.data
                    if (list != null && !list.isEmpty()) {
                        list.map { retList.add(it.copyToOrgVO()) }
                    }
                    retList
                }

                Observable.zip(unitObservable, identityObservable, { t1, t2 ->
                    val retList = ArrayList<NewContactListVO>()
                    retList.addAll(t1)
                    retList.addAll(t2)
                    retList
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(Action1<ArrayList<NewContactListVO>> { list -> mView?.callbackResult(list) },
                                ExceptionHandler(mView?.getContext()) { e -> mView?.backError(e.message ?: "") })

            }
        }
    }

    override fun searchPersonWithKey(result: String) {
            val form = PersonListLikeForm( result)
            val result = ArrayList<NewContactListVO>()
            getOrganizationAssembleControlApi(mView?.getContext())?.let { service ->
                service.personListLike(form)
                        .subscribeOn(Schedulers.io())
                        .map { response ->
                            val retList = ArrayList<NewContactListVO>()
                            val list = response.data
                            if (list != null && !list.isEmpty()) {
                                list.map {
                                    retList.add(it.copyToOrgVO())
                                }
                            }
                            retList
                        }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(Action1<ArrayList<NewContactListVO>> { list -> result.addAll(list) },
                                ExceptionHandler(mView?.getContext()) { e -> mView?.backError(e.message ?: "") },
                                Action0 { mView?.callbackResult(result) })
            }
        }
}
