package com.x.organization.assemble.authentication.wrap.out;

import com.x.base.core.gson.GsonPropertyObject;

public class WrapOutInitManagerCredential extends GsonPropertyObject {
	private String credential;
	private String password;

	public String getCredential() {
		return credential;
	}

	public void setCredential(String credential) {
		this.credential = credential;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
