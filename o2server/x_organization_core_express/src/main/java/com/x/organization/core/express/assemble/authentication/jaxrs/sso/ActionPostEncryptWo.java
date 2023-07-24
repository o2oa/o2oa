package com.x.organization.core.express.assemble.authentication.jaxrs.sso;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class ActionPostEncryptWo extends GsonPropertyObject {

	private static final long serialVersionUID = -8195894624223752270L;

	@FieldDescribe("用于登录的加密字串")
	private String token;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
