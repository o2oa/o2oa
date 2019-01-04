package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.meeting.edit

import android.widget.TextView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.meeting.MeetingInfoJson


object MeetingEditContract {
    interface View : BaseView {
        fun onError(message: String)
        fun updateMeetingSuccess()
        fun deleteMeetingSuccess()
        fun saveMeetingFileSuccess(fileName: String,fileId: String)
        fun deleteMeetingFile(position: Int)
    }

    interface Presenter : BasePresenter<View> {
        fun asyncLoadPersonName(nameTv: TextView, t: String)
        fun deleteMeeting(id: String)
        fun updateMeetingInfo(meeting: MeetingInfoJson)
        fun saveMeetingFile(fileId : String,meetingId : String)
        fun deleteMeetingFile(fileId : String,position : Int)
    }
}
