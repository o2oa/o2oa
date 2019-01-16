package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.message;

/**
 * type:
 * fileModify
 * fileShare
 *
 *
 * Created by FancyLou on 2016/8/10.
 */
public class YunpanFileMessage extends BaseMessage {

    private String attachment;//文件id

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }
}
