package com.x.base.core.project.jaxrs;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class WoTemporaryRedirect extends GsonPropertyObject {

	public WoTemporaryRedirect() {
	}

	public WoTemporaryRedirect(String url) throws Exception {
		this.url = url;
	}

	@FieldDescribe("地址")
	private String url;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
