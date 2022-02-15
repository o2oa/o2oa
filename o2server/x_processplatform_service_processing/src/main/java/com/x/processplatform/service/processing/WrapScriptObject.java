package com.x.processplatform.service.processing;

public class WrapScriptObject {

	private String value;

	private String type;

	public void type(String type) {
		this.type = type;
	}

	public String type() {
		return this.type;
	}

	public String get() {
		return this.value;
	}

	public void set(String value) {
		this.value = value;
	}
}
