package com.x.processplatform.core.express.service.processing.jaxrs.task;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class ActionProcessingWi extends GsonPropertyObject {

	private static final long serialVersionUID = -2272062779655632688L;

	public static final String PROCESSINGTYPE_TASK = "task";

	public static final String PROCESSINGTYPE_RESET = "reset";

	@FieldDescribe("流转类型.")
	private String processingType;

	@FieldDescribe("路由决策.")
	private String routeName;

	@FieldDescribe("待办意见.")
	private String opinion;

	public String getProcessingType() {
		return processingType;
	}

	public void setProcessingType(String processingType) {
		this.processingType = processingType;
	}

	public String getRouteName() {
		return routeName;
	}

	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}

	public String getOpinion() {
		return opinion;
	}

	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}

}
