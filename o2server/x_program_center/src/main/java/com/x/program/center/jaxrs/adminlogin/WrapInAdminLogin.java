package com.x.program.center.jaxrs.adminlogin;

import com.x.base.core.project.gson.GsonPropertyObject;

public class WrapInAdminLogin extends GsonPropertyObject {

	private String credential;

	private String password;

	private String code;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCredential() {
		return credential;
	}

	public void setCredential(String credential) {
		this.credential = credential;
	}

}
