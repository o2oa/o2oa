package com.x.base.core.project.http;

import com.x.base.core.project.gson.GsonPropertyObject;

public class WrapOutInteger extends GsonPropertyObject {

	public WrapOutInteger(Integer value) throws Exception {
		this.value = value;
	}

	public WrapOutInteger() {
	}

	private Integer value;

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}



}
