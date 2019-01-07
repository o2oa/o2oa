package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.message;

/**
 * type:
 * meetingAccept
 * meetingCancel
 * meetingInvite
 * meetingReject
 *
 * Created by FancyLou on 2016/8/10.
 */
public class MeetingMessage extends BaseMessage {

    private String building;//楼房id
    private String room;//会议室id
    private String meeting;//会议id

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getMeeting() {
        return meeting;
    }

    public void setMeeting(String meeting) {
        this.meeting = meeting;
    }
}
