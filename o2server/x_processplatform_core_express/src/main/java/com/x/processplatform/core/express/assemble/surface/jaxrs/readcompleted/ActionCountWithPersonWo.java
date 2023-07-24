package com.x.processplatform.core.express.assemble.surface.jaxrs.readcompleted;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionCountWithPersonWo extends GsonPropertyObject {

	private static final long serialVersionUID = 2387906569643215824L;

	@FieldDescribe("已阅数量.")
	@Schema(description = "已阅数量.")
	private Long count = 0L;

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

}