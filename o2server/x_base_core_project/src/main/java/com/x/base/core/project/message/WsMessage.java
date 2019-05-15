package com.x.base.core.project.message;

import com.google.gson.JsonElement;

public class WsMessage extends Message {

	public WsMessage() {

	}

	private String type;

	private String person;

	private String title;

	private JsonElement body;

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public JsonElement getBody() {
		return body;
	}

	public void setBody(JsonElement body) {
		this.body = body;
	}

}
