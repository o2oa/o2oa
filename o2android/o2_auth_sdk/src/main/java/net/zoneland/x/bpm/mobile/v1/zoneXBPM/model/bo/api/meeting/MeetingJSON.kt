package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.meeting

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.MeetingRoom
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.DateHelper
import java.io.Serializable


/**
 * Created by fancy on 2017/3/28.
 */

/**
 * 会议信息对象
 */
data class MeetingInfoJson(
        var createTime: String = "",
        var updateTime: String = "",
        var pinyin: String = "",
        var pinyinInitial: String = "",
        var id: String = "",
        var subject: String = "",//名称
        var summary: String = "",//说明
        var room: String = "",//会议室
        var date: String = "",//会议日期
        var startTime: String = "",//开始日期
        var completedTime: String = "",//结束日期
        var actualStartTime: String = "",//实际开始日期
        var actualCompletedTime: String = "",//实际结束日期
        var manualCompleted: Boolean = false,//会议是否手工结束
        var invitePersonList: List<String> = ArrayList(),//被邀请的人员
        var acceptPersonList: List<String> = ArrayList(),//接收的人员
        var rejectPersonList: List<String> = ArrayList(),//拒绝的人员
        var confirmStatus: String = "",//会议预定状态
        var applicant: String = "",//会议申请人
        var memo: String = "",//备注
        var myApply: Boolean = false,//是否我申请的
        var myWaitConfirm: Boolean = false,//是否需要我审核的
        var myWaitAccept: Boolean = false,//是否需要我受邀请的
        var myAccept: Boolean = false,//我已经接受邀请了
        var myReject: Boolean = false,//我已经拒绝邀请了
        var attachmentList : List<MeetingFileInfoJson> = ArrayList()
): Serializable

/**
 * 会议附件对象
 */
data class MeetingFileInfoJson(
        var createTime: String = "",
        var updateTime: String = "",
        var id: String = "",
        var meeting: String = "",
        var name: String = "",
        var extension: String = "",
        var storage: String = "",
        var length: Int = 0,
        var summary: Boolean = false,
        var lastUpdateTime: String = "",
        var lastUpdatePerson: String = ""
): Serializable

/**
 * 会议室对象
 */
data class RoomInfoJson(
        var createTime: String = "",
        var updateTime: String = "",
        var pinyin: String = "",
        var pinyinInitial: String = "",
        var id: String = "",
        var name: String = "",
        var building: String = "",
        var roomNumber: String = "",
        var device: String = "",//设备用#分割
        var auditor: String = "",
        var floor: Int = 0,
        var capacity: Int = 0,
        var available: Boolean = false,
        var idle: Boolean = false,//是否空闲
        var meetingList:List<MeetingInfoJson> = ArrayList()
): Serializable {
    /**
     * 对象copy给view对象
     */
    fun copyToVO() : MeetingRoom.Room {
        val number : Int = meetingList.count { DateHelper.isToday(DateHelper.convertStringToDate(it.startTime)) }
        return MeetingRoom.Room(id, name, roomNumber, device, floor,
                capacity, available, idle, number, building, meetingList)
    }

    fun copyToVO(startTime : String,endTime : String) : MeetingRoom.Room {
        val number : Int = meetingList.count { (it.startTime >= startTime && it.startTime < endTime)
                                                ||(it.completedTime > startTime && it.completedTime <= endTime)}
        return MeetingRoom.Room(id, name, roomNumber, device, floor,
                capacity, available, idle, number, building, meetingList)
    }
}

/**
 * 大楼对象
 */
data class BuildingInfoJson(
        var createTime: String = "",
        var updateTime: String = "",
        var pinyin: String = "",
        var pinyinInitial: String = "",
        var id: String = "",
        var name: String = "",
        var address: String = "",
        var roomList: List<RoomInfoJson> = ArrayList()
){
    /**
     * 对象copy给view对象
     */
    fun copyToVO() : MeetingRoom.Building {
        return MeetingRoom.Building(name,roomList.size)
    }
}

