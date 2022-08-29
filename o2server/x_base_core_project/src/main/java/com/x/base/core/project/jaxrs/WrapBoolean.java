package com.x.base.core.project.jaxrs;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class WrapBoolean extends GsonPropertyObject {

	private static final long serialVersionUID = 19308077998975323L;

	@FieldDescribe("布尔值.")
	@Schema(description = "布尔值.")
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
