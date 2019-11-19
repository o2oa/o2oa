package com.x.base.core.project.message;


public class PmsInnerMessage extends Message {

	private String person;
	private String message;

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
