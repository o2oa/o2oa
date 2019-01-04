package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.meeting.invited

import android.widget.TextView
import net.muliba.accounting.app.ExceptionHandler
import net.muliba.fancyfilepickerlibrary.ext.concat
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.ResponseHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by 73419 on 2017/8/2 0002.
 */
class MeetingInvitedFragmentPresenter : BasePresenterImpl<MeetingInvitedFragmentContract.View>(),
        MeetingInvitedFragmentContract.Presenter {

    override fun getReceiveInviteMeetingList() {
        getMeetingAssembleControlService(mView?.getContext())?.let { service ->
            service.myWaitAcceptMeetingList()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler { meetingInfoJsonList ->
                        mView?.loadReceiveInviteMeetingList(meetingInfoJsonList)
                    }, ExceptionHandler(mView?.getContext()) { e ->
                        XLog.error("查询信息失败, ", e)
                        //it.loadMyMeetingListFail()
                    })

        }
    }

    override fun getOriginatorMeetingList() {
        getMeetingAssembleControlService(mView?.getContext())?.let { service ->
            service.myOriginatorMeetingList()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler { meetingInfoJsonList ->
                        mView?.loadOriginatorMeetingList(meetingInfoJsonList)
                    }, ExceptionHandler(mView?.getContext()) { e ->
                        XLog.error("查询信息失败, ", e)
                        //it.loadMyMeetingListFail()
                    })

        }
    }

    override fun asyncLoadRoomName(roomTv: TextView, tag: String, room: String) {
        getMeetingAssembleControlService(mView?.getContext())?.let { service ->
            service.getRoomById(room)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler { info ->
                        if (roomTv.tag == tag)
                            roomTv.text = info.name
                    }, ExceptionHandler(mView?.getContext()) { e ->
                        XLog.error("", e)
                    })

        }
    }

    override fun acceptMeetingInvited(id: String) {
        getMeetingAssembleControlService(mView?.getContext())?.let { service ->
            service.acceptMeeting(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler { _ ->
                        mView?.refreshMeetingList()
                    }, ExceptionHandler(mView?.getContext()) { e ->
                        XLog.error("保存失败", e)
                        mView?.onError("操作失败，请稍后再试!")
                    })
        }
    }

    override fun rejectMeetingInvited(id: String) {
        getMeetingAssembleControlService(mView?.getContext())?.let { service ->
            service.rejectMeeting(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResponseHandler { _ ->
                        mView?.refreshMeetingList()
                    }, ExceptionHandler(mView?.getContext()) { e ->
                        XLog.error("保存失败", e)
                        mView?.onError("操作失败，请稍后再试")
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