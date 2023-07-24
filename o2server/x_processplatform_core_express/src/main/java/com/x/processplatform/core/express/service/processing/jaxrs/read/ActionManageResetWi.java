package com.x.processplatform.core.express.service.processing.jaxrs.read;

import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionManageResetWi extends GsonPropertyObject {

	private static final long serialVersionUID = -3686768943199603543L;

	@FieldDescribe("重置待阅人身份.")
	@Schema(description = "重置待阅人身份.")
	private List<String> identityList;

	@FieldDescribe("待阅意见.")
	@Schema(description = "待阅意见.")
	private String opinion;

	public List<String> getIdentityList() {
		return identityList;
	}

	public void setIdentityList(List<String> identityList) {
		this.identityList = identityList;
	}

	public String getOpinion() {
		return opinion;
	}

	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}

}