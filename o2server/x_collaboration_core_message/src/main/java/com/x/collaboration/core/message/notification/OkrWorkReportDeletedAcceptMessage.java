package com.x.collaboration.core.message.notification;


public class OkrWorkReportDeletedAcceptMessage extends NotificationMessage {

	public OkrWorkReportDeletedAcceptMessage( String person, String reportId, String reportTitle, String messageContent ) {
		super( NotificationType.okrWorkReportDeletedAccept, person );
		this.reportId = reportId;
		this.reportTitle = reportTitle;
		this.messageContent = messageContent;
	}

	private String reportId;
	private String reportTitle;
	private String messageContent;	
	
	
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
