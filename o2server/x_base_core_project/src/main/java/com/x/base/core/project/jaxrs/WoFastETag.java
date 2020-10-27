package com.x.base.core.project.jaxrs;

import com.x.base.core.project.gson.GsonPropertyObject;

public class WoFastETag extends GsonPropertyObject {

	private String fastETag;

	public String getFastETag() {
		return fastETag;
	}

	public void setFastETag(String fastETag) {
		this.fastETag = fastETag;
	}
}
