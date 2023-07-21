package com.x.processplatform.core.express.service.processing.jaxrs.read;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionCreateWithWorkCompletedWi extends GsonPropertyObject {

	private static final long serialVersionUID = 7061824634357637666L;

	@FieldDescribe("身份标识.")
	@Schema(description = "身份标识.")
	private List<String> identityList = new ArrayList<>();

	@FieldDescribe("发送待阅通知.")
	@Schema(description = "发送待阅通知.")
	private Boolean notify = false;

	public List<String> getIdentityList() {
		return identityList;
	}

	public void setIdentityList(List<String> identityList) {
		this.identityList = identityList;
	}

	public Boolean getNotify() {
		return notify;
	}

	public void setNotify(Boolean notify) {
		this.notify = notify;
	}

}