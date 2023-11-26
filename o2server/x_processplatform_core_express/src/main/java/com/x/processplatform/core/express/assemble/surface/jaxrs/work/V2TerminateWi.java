package com.x.processplatform.core.express.assemble.surface.jaxrs.work;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class V2TerminateWi extends GsonPropertyObject {

	private static final long serialVersionUID = 6045785174271544769L;

	@FieldDescribe("待办选择路由名称.")
	private String routeName;

	@FieldDescribe("待办办理意见.")
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