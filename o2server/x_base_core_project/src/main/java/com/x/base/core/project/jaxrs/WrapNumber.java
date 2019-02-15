package com.x.base.core.project.jaxrs;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class WrapNumber extends GsonPropertyObject {

	public WrapNumber() {

	}

	public WrapNumber(Number o) {
		this.value = o;
	}

	@FieldDescribe("数字值")
	private Number value;

	public Number getValue() {
		return value;
	}

	public void setValue(Number value) {
		this.value = value;
	}

}
