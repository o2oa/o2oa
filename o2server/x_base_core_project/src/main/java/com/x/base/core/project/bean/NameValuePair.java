package com.x.base.core.project.bean;

import com.x.base.core.project.gson.GsonPropertyObject;

public class NameValuePair extends GsonPropertyObject {

	public NameValuePair() {

	}

	public NameValuePair(String name, Object value) {
		this.name = name;
		this.value = value;
	}

	private String name;

	private Object value;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getValue() {
		return value;
	}

	public <T> T getValue(Class<T> clz) {
		return this.value == null ? null : (T) value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}
