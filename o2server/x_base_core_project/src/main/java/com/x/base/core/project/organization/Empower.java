package com.x.base.core.project.organization;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class Empower extends GsonPropertyObject {

	@FieldDescribe("身份")
	private String fromIdentity;

	@FieldDescribe("委托身份")
	private String toIdentity;

	public String getFromIdentity() {
		return fromIdentity;
	}

	public void setFromIdentity(String fromIdentity) {
		this.fromIdentity = fromIdentity;
	}

	public String getToIdentity() {
		return toIdentity;
	}

	public void setToIdentity(String toIdentity) {
		this.toIdentity = toIdentity;
	}

}
