package com.x.base.core.project.jaxrs;

import java.util.Objects;

import com.x.base.core.gson.GsonPropertyObject;

public class StringWo extends GsonPropertyObject {

	public StringWo() {

	}

	public StringWo(Object o) {
		this.value = Objects.toString(o, "");
	}

	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
