package com.x.collaboration.core.message.notification;


public class OkrCenterWorkDeployAcceptMessage extends NotificationMessage {

	public OkrCenterWorkDeployAcceptMessage( String person, String centerId, String centerTitle, String messageContent ) {
		super( NotificationType.okrCenterWorkDeployAccept, person );
		this.centerId = centerId;
		this.centerTitle = centerTitle;
		this.messageContent = messageContent;
	}

	private String centerId;
	private String centerTitle;
	private String messageContent;
	
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
