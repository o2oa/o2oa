package com.x.processplatform.core.express.assemble.surface.jaxrs.work;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionListCountWithProcessWo extends GsonPropertyObject {

	private static final long serialVersionUID = 3947731081213211239L;

	@FieldDescribe("流程标志.")
	@Schema(description = "流程标志.")
	private String value;

	@FieldDescribe("流程名称.")
	@Schema(description = "流程名称.")
	private String name;

	@FieldDescribe("数量.")
	@Schema(description = "数量.")
	private Long count;

	public ActionListCountWithProcessWo() {

	}

	public ActionListCountWithProcessWo(String value, String name, Long count) {
		this.value = value;
		this.name = name;
		this.count = count;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

}