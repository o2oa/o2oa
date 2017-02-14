package com.x.collaboration.core.message.notification;

public class TaskMessage extends NotificationMessage {

	private String work;
	private String task;

	public TaskMessage(String person, String work, String task) {
		super(NotificationType.task, person);
		this.work = work;
		this.task = task;
	}

	public String getWork() {
		return work;
	}

	public void setWork(String work) {
		this.work = work;
	}

	public String getTask() {
		return task;
	}

	public void setTask(String task) {
		this.task = task;
	}

}
