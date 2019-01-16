package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.message;

/**
 * type:
 * okrCenterWorkDeployAccept
 *
 *
 * Created by FancyLou on 2016/8/10.
 */
public class OkrCenterMessage extends BaseMessage {

    private String centerId;//中心工作id
    private String centerTitle;//工作名称
    private String messageContent;//消息内容

    public String getCenterId() {
        return centerId;
    }

    public void setCenterId(String centerId) {
        this.centerId = centerId;
    }

    public String getCenterTitle() {
        return centerTitle;
    }

    public void setCenterTitle(String centerTitle) {
        this.centerTitle = centerTitle;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }
}
