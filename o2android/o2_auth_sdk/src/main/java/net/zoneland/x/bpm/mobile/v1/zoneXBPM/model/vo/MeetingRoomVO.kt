package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.meeting.MeetingInfoJson
import java.io.Serializable

/**
 * Created by fancy on 2017/3/28.
 */


sealed class MeetingRoom: Serializable {

    class Building(val name: String, val roomNumber: Int) : MeetingRoom()
    class Room(val id: String, val name: String, val roomNumber: String, val device: String, val floor: Int,
               val capacity: Int, val available: Boolean, val idle: Boolean, val meetingNumber: Int,
               val building: String, val meetingList: List<MeetingInfoJson>): MeetingRoom(),Serializable
}