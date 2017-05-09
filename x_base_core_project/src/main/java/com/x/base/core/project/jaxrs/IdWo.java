package com.x.base.core.project.jaxrs;

import com.x.base.core.gson.GsonPropertyObject;

public class IdWo extends GsonPropertyObject {

	public IdWo() {
	}

	public IdWo(String id) throws Exception {
		this.id = id;
	}

	private String id;

	public String getId() {
		return id;
	}

}
