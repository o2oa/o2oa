package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.meeting.room

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.group.Group
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.MeetingRoom

/**
 * Created by 73419 on 2017/8/17 0017.
 */
interface MeetingRoomFragmentContract {

    interface View : BaseView{
        fun findBuildingList(list:List<Group<MeetingRoom.Building, MeetingRoom.Room>>)
        fun findError(message:String)
    }

    interface Presenter : BasePresenter<View>{
        fun  findBuildingListByTime(startTime: String, endTime: String)
    }
}