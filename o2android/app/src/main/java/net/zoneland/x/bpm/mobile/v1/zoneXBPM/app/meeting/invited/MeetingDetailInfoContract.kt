package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.meeting.invited

import android.widget.TextView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.meeting.MeetingFileInfoJson
import java.io.File

/**
 * Created by 73419 on 2017/8/4 0004.
 */
interface MeetingDetailInfoContract {

    interface View : BaseView {
        fun downloadAttachmentSuccess(file: File?)
    }

    interface Presenter : BasePresenter<View>{
        fun asyncLoadRoomName(roomTv: TextView, room: String)
        fun asyncLoadPersonName(nameTv: TextView, t: String)
        fun downloadMeetingFile(meetingFileInfoJson: MeetingFileInfoJson)
    }
}