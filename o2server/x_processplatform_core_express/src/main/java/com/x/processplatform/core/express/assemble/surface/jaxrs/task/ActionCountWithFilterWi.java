package com.x.processplatform.core.express.assemble.surface.jaxrs.task;

import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionCountWithFilterWi extends GsonPropertyObject {

	private static final long serialVersionUID = 5540904413841978134L;

	@FieldDescribe("人员")
	@Schema(description = "过滤限制人员列表")
	private List<String> credentialList;

	@FieldDescribe("应用")
	@Schema(description = "过滤限制应用列表")
	private List<String> appliationList;

	@FieldDescribe("流程")
	@Schema(description = "过滤限制流程列表")
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