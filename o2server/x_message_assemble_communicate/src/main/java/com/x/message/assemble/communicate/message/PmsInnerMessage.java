package com.x.message.assemble.communicate.message;

import com.google.gson.JsonObject;
import com.x.base.core.project.gson.GsonPropertyObject;
import java.util.Map;

public class PmsInnerMessage extends GsonPropertyObject {

	private static final long serialVersionUID = -833909003941196960L;

	private String person;
	private String message;

	private Map<String, String> stringExtras;
	private Map<String, Number> numberExtras;
	private Map<String, Boolean> booleanExtras;
	private Map<String, JsonObject> jsonExtras;

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

	public Map<String, String> getStringExtras() {
		return stringExtras;
	}

	public void setStringExtras(Map<String, String> stringExtras) {
		this.stringExtras = stringExtras;
	}

	public Map<String, Number> getNumberExtras() {
		return numberExtras;
	}

	public void setNumberExtras(Map<String, Number> numberExtras) {
		this.numberExtras = numberExtras;
	}

	public Map<String, Boolean> getBooleanExtras() {
		return booleanExtras;
	}

	public void setBooleanExtras(Map<String, Boolean> booleanExtras) {
		this.booleanExtras = booleanExtras;
	}

	public Map<String, JsonObject> getJsonExtras() {
		return jsonExtras;
	}

	public void setJsonExtras(Map<String, JsonObject> jsonExtras) {
		this.jsonExtras = jsonExtras;
	}
}