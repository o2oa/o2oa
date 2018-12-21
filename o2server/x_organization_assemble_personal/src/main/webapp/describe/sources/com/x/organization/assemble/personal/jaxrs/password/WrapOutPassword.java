package com.x.organization.assemble.personal.jaxrs.password;

import com.x.base.core.project.gson.GsonPropertyObject;

public class WrapOutPassword extends GsonPropertyObject {

	private String password;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
