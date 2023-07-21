package com.x.organization.core.express.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionLoginWi extends GsonPropertyObject {

	private static final long serialVersionUID = 633273224231633398L;

	@FieldDescribe("用户标识.")
	@Schema(description = "用户标识.")
	private String credential;

	@FieldDescribe("密码.")
	@Schema(description = "密码.")
	private String password;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCredential() {
		return credential;
	}

	public void setCredential(String credential) {
		this.credential = credential;
	}

}