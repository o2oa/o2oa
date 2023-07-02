package com.x.organization.core.express.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionCaptchaWo extends GsonPropertyObject {

	private static final long serialVersionUID = 7575159869222880938L;

	@FieldDescribe("标识.")
	@Schema(description = "标识.")
	private String id;

	@FieldDescribe("base64图片值.")
	@Schema(description = "base64图片值.")
	private String image;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

}