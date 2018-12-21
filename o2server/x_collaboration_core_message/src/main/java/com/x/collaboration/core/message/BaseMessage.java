package com.x.collaboration.core.message;

import java.util.Date;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.x.base.core.project.gson.GsonPropertyObject;

public class BaseMessage extends GsonPropertyObject {

	protected static String extractTextField(JsonElement jsonElement, String name) {
		if ((null != jsonElement) && jsonElement.isJsonObject()) {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			if (jsonObject.has(name)) {
				return jsonObject.get(name).getAsString();
			}
		}
		return null;
	}

	public static MessageCategory extractCategory(JsonElement jsonElement) {
		return MessageCategory.valueOf(extractTextField(jsonElement, FIELD_CATEGORY));
	}

	protected static String FIELD_CATEGORY = "category";

	protected BaseMessage(MessageCategory category) {
		this.time = new Date();
		this.category = category;
	}

	private Date time;

	private MessageCategory category;

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public MessageCategory getCategory() {
		return category;
	}

	public void setCategory(MessageCategory category) {
		this.category = category;
	}

}