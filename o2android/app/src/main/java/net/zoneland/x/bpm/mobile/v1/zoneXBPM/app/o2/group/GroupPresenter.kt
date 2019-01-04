package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.group

import android.text.TextUtils
import android.widget.TextView
import net.muliba.accounting.app.ExceptionHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.ResponseHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.PersonInfoData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.PersonalGroupData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Action1
import rx.schedulers.Schedulers

class GroupPresenter : BasePresenterImpl<GroupContract.View>(), GroupContract.Presenter {

    override fun loadGroupMembers(groupName: String) {
        getAssembleExpressApi(mView?.getContext())?.let { service->
            service.getGroupInfoData(groupName)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler<PersonalGroupData>({group -> mView?.loadGroupMembers(group.personList)}),
                            ExceptionHandler(mView?.getContext(), {}))
        }

    }

    override fun asyncLoadPersonMobileAndDepartment(person: String?, mobileTv: TextView?, deptTv: TextView?) {
        if (TextUtils.isEmpty(person) || mobileTv == null || deptTv == null ) {
            return
        }
        getAssembleExpressApi(mView?.getContext())?.let { service->
                    service.getPersonInfoData(person!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler<PersonInfoData>({ personData ->
                        if (person.equals(mobileTv.tag)){
                            mobileTv.text = personData.mobile ?: ""
                        }
                        if (person.equals(deptTv.tag)) {
                            if (personData.identityList!=null && !personData.identityList.isEmpty()) {
                                deptTv.text = personData.identityList[0].department ?: ""
                            }
                        }
                    }), Action1<Throwable>{ e-> XLog.error("", e)})
        }
    }
}
