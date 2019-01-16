package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.meeting.apply

import android.widget.TextView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.meeting.MeetingInfoJson


object MeetingApplyContract {
    interface View : BaseView {
        fun saveMeetingSuccess(id : String,fileId: String)
        fun saveMeetingFileSuccess(id : String)
        fun updateMeetingSuccess()
        fun deleteMeetingFile(fileId : String,position : Int)
        fun doMeetingFail(message:String)
    }

    interface Presenter : BasePresenter<View> {
        fun asyncLoadPersonName(tv:TextView, id:String)
        fun saveMeeting(info: MeetingInfoJson, meetingFile : String)
        fun saveMeetingNoFile(info: MeetingInfoJson)
        fun updateMeetingInfo(meeting: MeetingInfoJson,meetingId : String)
        fun saveMeetingFile(fileId : String,meetingId : String)
        fun deleteMeetingFile(fileId : String,position : Int)
    }
}
