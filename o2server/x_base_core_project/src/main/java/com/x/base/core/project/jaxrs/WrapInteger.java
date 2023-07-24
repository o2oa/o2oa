package com.x.base.core.project.jaxrs;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public   class WrapInteger extends GsonPropertyObject {

	public WrapInteger() {

	}

	public WrapInteger(Integer o) {
		this.value = o;
	}

	@FieldDescribe("整型值")
	private Integer value;

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

}
