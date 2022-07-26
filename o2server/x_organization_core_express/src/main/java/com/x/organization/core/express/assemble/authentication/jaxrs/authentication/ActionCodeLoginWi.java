package com.x.organization.core.express.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionCodeLoginWi extends GsonPropertyObject {

	private static final long serialVersionUID = -808566215993013029L;

	@FieldDescribe("用户标识.")
	@Schema(description = "用户标识.")
	private String credential;

	@FieldDescribe("短信认证码.")
	@Schema(description = "短信认证码.")
	private String codeAnswer;

	public String getCredential() {
		return credential;
	}

	public void setCredential(String credential) {
		this.credential = credential;
	}

	public String getCodeAnswer() {
		return codeAnswer;
	}

	public void setCodeAnswer(String codeAnswer) {
		this.codeAnswer = codeAnswer;
	}

}
