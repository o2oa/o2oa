package com.x.base.core.project.jaxrs;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public abstract class WoValue extends GsonPropertyObject {

	public WoValue() {
	}

	public WoValue(Object value) throws Exception {
		this.value = value;
	}

	@FieldDescribe("对象")
	private Object value;

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}
