package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.person

import net.muliba.accounting.app.ExceptionHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.ResponseHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.realm.RealmDataService
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.person.PersonJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class PersonPresenter : BasePresenterImpl<PersonContract.View>(), PersonContract.Presenter {

    override fun loadPersonInfo(name: String) {
        getOrganizationAssembleControlApi(mView?.getContext())?.let { service ->
            service.person(name)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler<PersonJson>({ person -> mView?.loadPersonInfo(person) }),
                            ExceptionHandler(mView?.getContext(), { e -> mView?.loadPersonInfoFail() }))
        }
    }

    override fun collectionUsuallyPerson(owner: String, person: String, ownerDisplay: String, personDisplay: String, gender: String, mobile: String) {
        RealmDataService().saveUsuallyPerson(owner, person, ownerDisplay, personDisplay, gender, mobile)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }

    override fun deleteUsuallyPerson(owner: String, person: String) {
        RealmDataService().deleteUsuallyPerson(owner, person).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }

    override fun isUsuallyPerson(owner: String, person: String) {
        mView?.let {
            RealmDataService().isUsuallyPerson(owner, person)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ data -> it.isUsuallyPerson(data) }, { e ->
                        XLog.error("", e)
                        it.isUsuallyPerson(false)
                    })
        }

    }
}
