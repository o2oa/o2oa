package com.x.collaboration.core.message.notification;

import com.google.gson.JsonElement;
import com.x.collaboration.core.message.BaseMessage;
import com.x.collaboration.core.message.MessageCategory;

public abstract class NotificationMessage extends BaseMessage {

	public static String extractPerson(JsonElement jsonElement) {
		return extractTextField(jsonElement, FIELD_PERSON);
	}

	public static NotificationType extractType(JsonElement jsonElement) {
		return NotificationType.valueOf(extractTextField(jsonElement, FIELD_TYPE));
	}

	protected static String FIELD_PERSON = "person";
	protected static String FIELD_TYPE = "type";

	private NotificationType type;
	private String person;

	public NotificationMessage(NotificationType type, String person) {
		super(MessageCategory.notification);
		this.type = type;
		this.person = person;
	}

	public NotificationType getType() {
		return type;
	}

	public void setType(NotificationType type) {
		this.type = type;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

}
