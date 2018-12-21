package com.x.collaboration.core.message.notification;

public class FileShareMessage extends NotificationMessage {

	private String attachment;

	public FileShareMessage(String person, String attachment) {
		super(NotificationType.fileShare, person);
		this.attachment = attachment;
	}

	public String getAttachment() {
		return attachment;
	}

	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}

}
