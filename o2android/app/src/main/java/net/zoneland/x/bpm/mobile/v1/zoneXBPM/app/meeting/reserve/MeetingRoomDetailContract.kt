package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.meeting.reserve

import android.widget.TextView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.meeting.BuildingInfoJson

/**
 * Created by 73419 on 2017/8/17 0017.
 */
interface MeetingRoomDetailContract {

    interface View : BaseView {
        fun getBuildingName( buildingInfoJson: BuildingInfoJson)
    }

    interface Presenter : BasePresenter<View> {
        fun asyncLoadRoomName(roomTv: TextView, id: String, room: String)
        fun asyncLoadPersonName(personTv: TextView, tag: String, person: String)
        fun getBuildingDetailById(id : String)
    }
}