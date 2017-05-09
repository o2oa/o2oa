package com.x.base.core.project.jaxrs;

import com.x.base.core.gson.GsonPropertyObject;

public class CaptchaWo extends GsonPropertyObject {

	private String id;
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
