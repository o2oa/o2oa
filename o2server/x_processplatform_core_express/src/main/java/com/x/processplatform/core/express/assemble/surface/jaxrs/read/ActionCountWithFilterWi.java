package com.x.processplatform.core.express.assemble.surface.jaxrs.read;

import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionCountWithFilterWi extends GsonPropertyObject {

	private static final long serialVersionUID = -5626519123284286315L;

	@FieldDescribe("人员标识.")
	@Schema(description = "人员标识.")
	private List<String> credentialList;

	@FieldDescribe("应用标识.")
	@Schema(description = "应用标识.")
	private List<String> appliationList;

	@FieldDescribe("流程标识.")
	@Schema(description = "流程标识.")
	private List<String> processList;

	public List<String> getCredentialList() {
		return credentialList;
	}

	public void setCredentialList(List<String> credentialList) {
		this.credentialList = credentialList;
	}

	public List<String> getAppliationList() {
		return appliationList;
	}

	public void setAppliationList(List<String> appliationList) {
		this.appliationList = appliationList;
	}

	public List<String> getProcessList() {
		return processList;
	}

	public void setProcessList(List<String> processList) {
		this.processList = processList;
	}

}