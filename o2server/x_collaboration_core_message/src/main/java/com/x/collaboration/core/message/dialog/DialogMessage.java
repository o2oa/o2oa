package com.x.collaboration.core.message.dialog;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.collaboration.core.message.BaseMessage;
import com.x.collaboration.core.message.MessageCategory;

public abstract class DialogMessage extends BaseMessage {

	protected static String FIELD_PERSON = "person";
	protected static String FIELD_TYPE = "type";
	protected static String FIELD_FROM = "from";

	private DialogType type;
	private String from;
	private String person;

	public DialogMessage(DialogType type, String from, String person) {
		super(MessageCategory.dialog);
		this.type = type;
		this.from = from;
		this.person = person;
	}

	public static String extractFrom(JsonElement jsonElement) {
		return extractTextField(jsonElement, FIELD_FROM);
	}

	public static String extractPerson(JsonElement jsonElement) {
		return extractTextField(jsonElement, FIELD_PERSON);
	}

	public static DialogType extractType(JsonElement jsonElement) {
		String value = extractTextField(jsonElement, FIELD_TYPE);
		if (StringUtils.isNotEmpty(value)) {
			return DialogType.valueOf(value);
		}
		return null;
	}

	public DialogType getType() {
		return type;
	}

	public void setType(DialogType type) {
		this.type = type;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

}
