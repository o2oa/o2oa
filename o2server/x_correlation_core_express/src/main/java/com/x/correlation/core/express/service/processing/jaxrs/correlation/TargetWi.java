package com.x.correlation.core.express.service.processing.jaxrs.correlation;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class TargetWi extends GsonPropertyObject {

	private static final long serialVersionUID = 395825437810549953L;

	@FieldDescribe("关联目标类型.")
	private String targetType;

	@FieldDescribe("关联目标标识.")
	private String targetBundle;

	public String getTargetType() {
		return targetType;
	}

	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}

	public String getTargetBundle() {
		return targetBundle;
	}

	public void setTargetBundle(String targetBundle) {
		this.targetBundle = targetBundle;
	}

}