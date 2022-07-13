package com.x.processplatform.core.express.service.processing.jaxrs.process;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionListWithPersonWithApplicationFilterWi extends GsonPropertyObject {

	private static final long serialVersionUID = -2305165705076347869L;

	@FieldDescribe("可启动流程终端类型,可选值 client,mobile,all.")
	@Schema(description = "可启动流程终端类型,可选值 client,mobile,all.")
	private String startableTerminal;

	public String getStartableTerminal() {
		return startableTerminal;
	}

	public void setStartableTerminal(String startableTerminal) {
		this.startableTerminal = startableTerminal;
	}
}