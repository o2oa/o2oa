package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.main

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2CustomStyle
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.enums.ApplicationEnum
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.realm.RealmDataService
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.edit
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by fancy on 2017/6/8.
 */

class MainPresenter : BasePresenterImpl<MainContract.View>(), MainContract.Presenter {

    override fun checkO2AIEnable() {
        RealmDataService().findAllNativeApp().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .o2Subscribe {
                    onNext {
                        mView?.o2AIEnable(it.any {
                            it.enable && it.key == ApplicationEnum.O2AI.key
                        })
                    }
                    onError { e, isNetworkError ->
                        XLog.error("o2ai check error $isNetworkError", e)
                        mView?.o2AIEnable(false)
                    }
                }
    }

    override fun checkAttendanceFeature() {
        getAttendanceAssembleControlService(mView?.getContext())?.let {
            service ->
            service.listMyRecords().subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .o2Subscribe {
                        onNext {
                            val data = it.data
                            if (data?.scheduleInfos != null && data.scheduleInfos.isNotEmpty()) {
                                O2SDKManager.instance().prefs().edit {
                                    putString(O2.PRE_ATTENDANCE_VERSION_KEY, "1");
                                }
                            }else {
                                O2SDKManager.instance().prefs().edit {
                                    putString(O2.PRE_ATTENDANCE_VERSION_KEY, "0");
                                }
                            }
                        }
                        onError { e, _ ->
                            XLog.error("", e)
                            O2SDKManager.instance().prefs().edit {
                                putString(O2.PRE_ATTENDANCE_VERSION_KEY, "0");
                            }
                        }
                    }
        }
    }
}