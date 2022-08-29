package com.x.processplatform.core.express.assemble.surface.jaxrs.anonymous;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionTaskCountWithPersonWo extends GsonPropertyObject {

	private static final long serialVersionUID = 8792811593252273112L;

	@FieldDescribe("待办数量.")
	@Schema(description = "待办数量.")
	private Long count = 0L;

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

}