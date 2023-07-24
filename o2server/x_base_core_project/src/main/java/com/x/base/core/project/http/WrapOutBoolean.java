package com.x.base.core.project.http;

import com.x.base.core.project.gson.GsonPropertyObject;

public class WrapOutBoolean extends GsonPropertyObject {

	private Boolean value;

	public static WrapOutBoolean trueInstance() {
		WrapOutBoolean o = new WrapOutBoolean();
		o.setValue(true);
		return o;
	}

	public static WrapOutBoolean falseInstance() {
		WrapOutBoolean o = new WrapOutBoolean();
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
