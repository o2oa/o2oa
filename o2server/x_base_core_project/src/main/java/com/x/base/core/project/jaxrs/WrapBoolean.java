package com.x.base.core.project.jaxrs;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class WrapBoolean extends GsonPropertyObject {

	@FieldDescribe("布尔值.")
	private Boolean value;

	public WrapBoolean() {

	}

	public WrapBoolean(Boolean value) {
		this.value = value;
	}

	public Boolean getValue() {
		return value;
	}

	public void setValue(Boolean value) {
		this.value = value;
	}

}
