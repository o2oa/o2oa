package com.x.cms.assemble.control.jaxrs.image;

import com.x.base.core.project.gson.GsonPropertyObject;

public class WrapInImage extends GsonPropertyObject {

	private String url;

	private Integer size;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

}
