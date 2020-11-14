package com.x.base.core.project.jaxrs;

import com.x.base.core.project.gson.GsonPropertyObject;

public class WoMaxAgeFastETag extends GsonPropertyObject {

	private static final long serialVersionUID = -8408799363043605326L;

	private String fastETag;

	private Integer maxAge = null;

	public String getFastETag() {
		return fastETag;
	}

	public void setFastETag(String fastETag) {
		this.fastETag = fastETag;
	}

	public Integer getMaxAge() {
		return maxAge;
	}

	public void setMaxAge(Integer maxAge) {
		this.maxAge = maxAge;
	}
}
