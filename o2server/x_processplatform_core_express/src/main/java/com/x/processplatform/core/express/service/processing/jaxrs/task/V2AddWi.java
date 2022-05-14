package com.x.processplatform.core.express.service.processing.jaxrs.task;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class V2AddWi extends GsonPropertyObject {

	private static final long serialVersionUID = -3293122515327864483L;

	@FieldDescribe("身份")
	private List<String> identityList = new ArrayList<>();

	@FieldDescribe("加入在指定位置之后,否则加入在指定位置之前.")
	private Boolean after;

	@FieldDescribe("是否执行替换")
	private Boolean replace;

	@FieldDescribe("是否删除")
	private String task;

	public List<String> getIdentityList() {
		return identityList;
	}

	public void setIdentityList(List<String> identityList) {
		this.identityList = identityList;
	}

	public Boolean getReplace() {
		return BooleanUtils.isTrue(replace);
	}

	public void setReplace(Boolean replace) {
		this.replace = replace;
	}

	public String getTask() {
		return task;
	}

	public void setTask(String task) {
		this.task = task;
	}

	public Boolean getAfter() {
		return BooleanUtils.isTrue(after);
	}

	public void setAfter(Boolean after) {
		this.after = after;
	}

}