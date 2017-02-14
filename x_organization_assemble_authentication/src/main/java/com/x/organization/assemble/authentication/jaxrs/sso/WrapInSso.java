package com.x.organization.assemble.authentication.jaxrs.sso;

import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.http.annotation.Wrap;

@Wrap
public class WrapInSso extends GsonPropertyObject {

	private String token;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	
}
