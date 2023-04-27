package com.x.meeting.core.entity;

/**
 *
 * @author sword
 */
public enum MeetingModeEnum {

	ONLINE("online", "线上会议"), OFFLINE("offline", "线下会议");
	private String value;
	private String name;

	private MeetingModeEnum(String value, String name) {
		this.value = value;
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value == null ? null : value.trim();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name == null ? null : name.trim();
	}

}
