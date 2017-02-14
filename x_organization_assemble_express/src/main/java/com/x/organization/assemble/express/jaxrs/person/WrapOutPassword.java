package com.x.organization.assemble.express.jaxrs.person;

import com.x.base.core.gson.GsonPropertyObject;

public class WrapOutPassword extends GsonPropertyObject {

	private String password;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
