package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.message;

/**
 * type:
 * okrWorkDeletedAccept
 * okrWorkDeployAccept
 * okrWorkGetAccept
 *
 * Created by FancyLou on 2016/8/10.
 */
public class OkrWorkMessage extends BaseMessage {

    private String workId;//工作id
    private String workTitle;//工作名称
    private String messageContent;//消息内容

    public String getWorkId() {
        return workId;
    }

    public void setWorkId(String workId) {
        this.workId = workId;
    }

    public String getWorkTitle() {
        return workTitle;
    }

    public void setWorkTitle(String workTitle) {
        this.workTitle = workTitle;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }
}
