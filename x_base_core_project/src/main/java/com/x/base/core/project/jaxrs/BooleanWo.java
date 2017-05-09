package com.x.base.core.project.jaxrs;

import com.x.base.core.gson.GsonPropertyObject;

public class BooleanWo extends GsonPropertyObject {

	private Boolean value;

	public BooleanWo() {

	}

	public BooleanWo(Boolean value) {
		this.value = value;
	}

	public static BooleanWo trueInstance() {
		BooleanWo o = new BooleanWo();
		o.setValue(true);
		return o;
	}

	public static BooleanWo falseInstance() {
		BooleanWo o = new BooleanWo();
		o.setValue(false);
		return o;
	}

	public Boolean getValue() {
		return value;
	}

	public void setValue(Boolean value) {
		this.value = value;
	}

}
