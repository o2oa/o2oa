package com.x.base.core.project.jaxrs;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class WoId extends GsonPropertyObject {

	public WoId() {
	}

	public WoId(String id) throws Exception {
		this.id = id;
	}

	@FieldDescribe("id")
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
