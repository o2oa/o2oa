package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.meeting.reserve

import android.widget.TextView
import net.muliba.accounting.app.ExceptionHandler
import net.muliba.fancyfilepickerlibrary.ext.concat
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.ResponseHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by 73419 on 2017/8/17 0017.
 */
class MeetingRoomDetailPresenter : BasePresenterImpl<MeetingRoomDetailContract.View>(),
        MeetingRoomDetailContract.Presenter {

    override fun asyncLoadRoomName(roomTv: TextView, id: String, room: String) {
        getMeetingAssembleControlService(mView?.getContext())?.let { service ->
            service.getRoomById(room).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler { info ->
                        val tag = roomTv.tag as String
                        if (id == tag) {
                            roomTv.text = info.name
                        }
                    }, ExceptionHandler(mView?.getContext()) { e ->
                        XLog.error("", e)
                    })

        }
    }

    override fun getBuildingDetailById(id: String) {
        getMeetingAssembleControlService(mView?.getContext())?.let { service ->
            service.getBuildingDetail(id).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler { info ->
                        mView?.getBuildingName(info)
                    }, ExceptionHandler(mView?.getContext()) { e ->
                        XLog.error("", e)
                    })

        }
    }

    override fun asyncLoadPersonName(personTv: TextView, tag: String, person: String) {
        getOrganizationAssembleControlApi(mView?.getContext())?.let { service ->
            service.person(person)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler { info ->
                        if (personTv.tag == tag) {
                            if (tag.contains("%%%"))
                                personTv.text = info.name
                            else
                                personTv.text = personTv.text.toString().concat(info.name).concat(" ")
                        }
                    }, ExceptionHandler(mView?.getContext()) { e ->
                        XLog.error("", e)
                    })
        }
    }

}