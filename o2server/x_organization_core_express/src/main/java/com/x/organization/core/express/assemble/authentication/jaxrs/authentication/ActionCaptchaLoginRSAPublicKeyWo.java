package com.x.organization.core.express.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionCaptchaLoginRSAPublicKeyWo extends GsonPropertyObject {

	private static final long serialVersionUID = 8305484005167999793L;

	@FieldDescribe("RSA公钥.")
	@Schema(description = "RSA公钥.")
	private String publicKey;

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}
}