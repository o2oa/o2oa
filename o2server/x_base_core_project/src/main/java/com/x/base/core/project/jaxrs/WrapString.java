package com.x.base.core.project.jaxrs;

import java.util.Objects;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class WrapString extends GsonPropertyObject {

	public WrapString() {

	}

	public WrapString(Object o) {
		this.value = Objects.toString(o, "");
	}

	@FieldDescribe("字符串值")
	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
