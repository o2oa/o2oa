package com.x.processplatform.core.express.service.processing.jaxrs.task;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class WrapProcessing extends GsonPropertyObject {

	private static final long serialVersionUID = -2272062779655632688L;

	public static final String PROCESSINGTYPE_TASK = "task";

	public static final String PROCESSINGTYPE_RESET = "reset";

//	@FieldDescribe("当前处理人")
//	private String processingPerson;
//
//	@FieldDescribe("当前处理身份")
//	private String processingIdentity;

	@FieldDescribe("流转类型.")
	private String processingType;

	public String getProcessingType() {
		return processingType;
	}

	public void setProcessingType(String processingType) {
		this.processingType = processingType;
	}

//	public String getProcessingPerson() {
//		return processingPerson;
//	}
//
//	public void setProcessingPerson(String processingPerson) {
//		this.processingPerson = processingPerson;
//	}
//
//	public String getProcessingIdentity() {
//		return processingIdentity;
//	}
//
//	public void setProcessingIdentity(String processingIdentity) {
//		this.processingIdentity = processingIdentity;
//	}

}
