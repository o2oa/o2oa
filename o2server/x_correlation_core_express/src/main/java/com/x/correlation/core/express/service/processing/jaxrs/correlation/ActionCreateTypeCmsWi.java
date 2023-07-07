package com.x.correlation.core.express.service.processing.jaxrs.correlation;

import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class ActionCreateTypeCmsWi extends GsonPropertyObject {

	private static final long serialVersionUID = -5876504008709428039L;

	@FieldDescribe("用户.")
	private String person;

	@FieldDescribe("关联目标.")
	private List<TargetWi> targetList;

	public List<TargetWi> getTargetList() {
		return targetList;
	}

	public void setTargetList(List<TargetWi> targetList) {
		this.targetList = targetList;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

}