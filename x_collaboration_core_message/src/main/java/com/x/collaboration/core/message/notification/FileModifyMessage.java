package com.x.collaboration.core.message.notification;

public class FileModifyMessage extends NotificationMessage {

	private String attachment;

	public FileModifyMessage(String person, String attachment) {
		super(NotificationType.fileModify, person);
		this.attachment = attachment;
	}

	public String getAttachment() {
		return attachment;
	}

	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}

}
