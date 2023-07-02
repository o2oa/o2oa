package com.x.processplatform.core.express.service.processing.jaxrs.keylock;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionLockWo extends GsonPropertyObject {

	private static final long serialVersionUID = 1864818073446091726L;

	@FieldDescribe("是否成功.")
	@Schema(description = "是否成功.")
	private Boolean success;

	@FieldDescribe("锁定人员.")
	@Schema(description = "锁定人员.")
	private String person;

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

}