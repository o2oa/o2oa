package com.x.processplatform.core.express.service.processing.jaxrs.task;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class WrapProcessing extends GsonPropertyObject {

	public static final String PROCESSINGTYPE_TASK = "task";

	public static final String PROCESSINGTYPE_RESET = "reset";

	@FieldDescribe("流转类型.")
	private String processingType;

	public String getProcessingType() {
		return processingType;
	}

	public void setProcessingType(String processingType) {
		this.processingType = processingType;
	}

}
