package com.x.processplatform.core.express.assemble.surface.jaxrs.task;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionManageProcessingWi extends GsonPropertyObject {

	private static final long serialVersionUID = -3684461752407390735L;

	@FieldDescribe("办理选择路由名称.")
	@Schema(description = "办理选择路由名称.")
	private String routeName;
	@FieldDescribe("办理意见.")
	@Schema(description = "办理意见.")
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
