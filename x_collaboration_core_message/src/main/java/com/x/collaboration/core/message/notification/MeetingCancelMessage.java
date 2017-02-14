package com.x.collaboration.core.message.notification;

public class MeetingCancelMessage extends NotificationMessage {

	public MeetingCancelMessage(String person, String building, String room, String meeting) {
		super(NotificationType.meetingCancel, person);
		this.building = building;
		this.room = room;
		this.meeting = meeting;
	}

	private String building;
	private String room;
	private String meeting;

	public String getMeeting() {
		return meeting;
	}

	public void setMeeting(String meeting) {
		this.meeting = meeting;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public String getBuilding() {
		return building;
	}

	public void setBuilding(String building) {
		this.building = building;
	}

}
