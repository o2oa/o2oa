package com.x.base.core.project.jaxrs;

import java.util.Objects;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class WrapString extends GsonPropertyObject {

	private static final long serialVersionUID = 4268038806585280159L;

	public WrapString() {

	}

	public WrapString(Object o) {
		this.value = Objects.toString(o, "");
	}

	@FieldDescribe("字符串值.")
	@Schema(description = "字符串值.")
	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
