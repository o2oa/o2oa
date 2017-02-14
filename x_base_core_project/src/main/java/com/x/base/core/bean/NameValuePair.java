package com.x.base.core.bean;

import com.x.base.core.gson.GsonPropertyObject;

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

	public void setValue(Object value) {
		this.value = value;
	}

}
