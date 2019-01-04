package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.message;

/**
 * type:
 * attendanceAppealAccept
 * attendanceAppealCancel
 * attendanceAppealInvite
 * attendanceAppealReject
 *
 * Created by FancyLou on 2016/8/10.
 */
public class AttendanceAppealMessage extends BaseMessage {

    private String id;//考勤申述id
    private String detailId;// 考勤明细id
    private String messageContent;// 消息内容

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDetailId() {
        return detailId;
    }

    public void setDetailId(String detailId) {
        this.detailId = detailId;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }
}
