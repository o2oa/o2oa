package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.meeting.main

import android.widget.TextView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.identity.ProcessWOIdentityJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.meeting.MeetingInfoJson


object MeetingMainFragmentContract {

    interface View : BaseView {
        fun findMyMeetingByDay(list: List<MeetingInfoJson>)
        fun onException(message: String)
        fun findMyMeetingByMonth(list: List<MeetingInfoJson>)
        fun setMeetingConfig(config: String)
        fun loadCurrentPersonIdentity(list: List<ProcessWOIdentityJson>)
        fun checkViewerBack(isDay: Boolean, result: Boolean)
        fun startProcessSuccess(workId:String)
        fun startProcessFail(message:String)
    }

    interface Presenter : BasePresenter<View> {
        fun findMyMeetingByDay(year: String, month: String, day: String, isViewer: Boolean)
        fun findMyMeetingByMonth(monthDate: String, isWeek: Boolean, isViewer: Boolean)
        fun asyncLoadRoomName(roomTv: TextView, id: String, room: String)
        fun asyncLoadPersonName(personTv: TextView, tag: String, person: String)
        fun getMeetingConfig()
        fun checkViewer(isDay: Boolean, config: String)
        fun loadCurrentPersonIdentityWithProcess(processId: String)
        fun startProcess(title: String, identifyId: String, processId: String)
    }
}
