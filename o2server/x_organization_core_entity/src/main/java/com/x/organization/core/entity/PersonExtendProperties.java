package com.x.organization.core.entity;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;

public class PersonExtendProperties extends JsonProperties {

	private static final long serialVersionUID = 8347025256346636346L;

	@FieldDescribe("扩展数据.")
	private JsonElement extend;

	public JsonElement getExtend() {
		return extend;
	}

	public void setExtend(JsonElement extend) {
		this.extend = extend;
	}

}
