package com.x.processplatform.core.express.service.processing.jaxrs.record;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class ActionWorkTerminateWi extends GsonPropertyObject {

	private static final long serialVersionUID = -6499166223522045961L;

	@FieldDescribe("路由.")
	private String routeName;
	@FieldDescribe("办理意见.")
	private String opinion;
	@FieldDescribe("处理人组织专有标识.")
	private String distinguishedName;
	@FieldDescribe("已完成工作标识.")
	private String workCompleted;

	public String getWorkCompleted() {
		return workCompleted;
	}

	public void setWorkCompleted(String workCompleted) {
		this.workCompleted = workCompleted;
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

	public String getDistinguishedName() {
		return distinguishedName;
	}

	public void setDistinguishedName(String distinguishedName) {
		this.distinguishedName = distinguishedName;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
