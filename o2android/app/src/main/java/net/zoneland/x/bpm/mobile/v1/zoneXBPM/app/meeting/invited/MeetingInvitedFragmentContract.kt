package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.meeting.invited

import android.widget.TextView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.meeting.MeetingInfoJson

/**
 * Created by 73419 on 2017/8/2 0002.
 */
interface MeetingInvitedFragmentContract {

    interface View : BaseView{
        fun loadReceiveInviteMeetingList(meetingList : List<MeetingInfoJson>)
        fun loadOriginatorMeetingList(meetingList : List<MeetingInfoJson>)
        fun refreshMeetingList()
        fun onError(message: String)

    }

    interface Presenter : BasePresenter<View>{
        fun getReceiveInviteMeetingList()
        fun getOriginatorMeetingList()
        fun asyncLoadRoomName(roomTv: TextView,tag: String, room: String)
        fun asyncLoadPersonName(personTv: TextView,tag: String, person: String)
        fun acceptMeetingInvited(id: String)
        fun rejectMeetingInvited(id: String)
    }

}