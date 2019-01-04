package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.message;

/**
 * type:
 * okrWorkReportDeletedAccept
 *
 * Created by FancyLou on 2016/8/10.
 */
public class OkrReportMessage extends BaseMessage {


    private String reportId;//工作汇报id
    private String reportTitle;//工作汇报名称
    private String messageContent;//消息内容

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getReportTitle() {
        return reportTitle;
    }

    public void setReportTitle(String reportTitle) {
        this.reportTitle = reportTitle;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }
}
