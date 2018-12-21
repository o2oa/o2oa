package com.x.collaboration.core.message.notification;

public class ReadMessage extends NotificationMessage {

	private String work;
	private String read;

	public ReadMessage(String person, String work, String read) {
		super(NotificationType.read, person);
		this.work = work;
		this.read = read;
	}

	public String getWork() {
		return work;
	}

	public void setWork(String work) {
		this.work = work;
	}

	public String getRead() {
		return read;
	}

	public void setRead(String read) {
		this.read = read;
	}

}
