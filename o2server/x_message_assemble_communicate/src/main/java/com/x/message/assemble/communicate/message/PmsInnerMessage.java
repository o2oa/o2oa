package com.x.message.assemble.communicate.message;

import com.x.base.core.project.gson.GsonPropertyObject;

public class PmsInnerMessage extends GsonPropertyObject {

	private static final long serialVersionUID = -833909003941196960L;

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