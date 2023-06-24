package com.x.correlation.core.express.service.processing.jaxrs.correlation;

import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class ActionCreateWi extends GsonPropertyObject {

	private static final long serialVersionUID = -6174739726994185000L;

	@FieldDescribe("源文档标识.")
	private String fromBundle;

	@FieldDescribe("源文档类型.")
	private String fromType;

	@FieldDescribe("关联目标.")
	private List<TargetWi> targetList;

	public String getFromBundle() {
		return fromBundle;
	}

	public void setFromBundle(String fromBundle) {
		this.fromBundle = fromBundle;
	}

	public String getFromType() {
		return fromType;
	}

	public void setFromType(String fromType) {
		this.fromType = fromType;
	}

	public List<TargetWi> getTargetList() {
		return targetList;
	}

	public void setTargetList(List<TargetWi> targetList) {
		this.targetList = targetList;
	}

}