package com.x.processplatform.core.express.service.processing.jaxrs.process;

import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionListWithProcessWi extends GsonPropertyObject {

	private static final long serialVersionUID = -6093458504525871536L;

	@FieldDescribe("流程(多值逗号隔开).")
	@Schema(description = "流程(多值逗号隔开).")
	private List<String> processList;

	@FieldDescribe("是否同时查询同版本的流程(true|false).")
	@Schema(description = "是否同时查询同版本的流程(true|false).")
	private boolean includeEdition;

	public List<String> getProcessList() {
		return processList;
	}

	public void setProcessList(List<String> processList) {
		this.processList = processList;
	}

	public boolean isIncludeEdition() {
		return includeEdition;
	}

	public void setIncludeEdition(boolean includeEdition) {
		this.includeEdition = includeEdition;
	}
}