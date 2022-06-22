package com.x.processplatform.core.express.service.processing.jaxrs.task;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class V2EditWi extends GsonPropertyObject {

	private static final long serialVersionUID = -8631082471633729236L;

	@FieldDescribe("选择的路由名称")
	@Schema(description = "选择的路由名称")
	private String routeName;

	@FieldDescribe("意见")
	@Schema(description = "意见")
	private String opinion;

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