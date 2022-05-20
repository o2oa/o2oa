package com.x.processplatform.core.express.assemble.surface.jaxrs.work;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class V2AddTaskWi extends GsonPropertyObject {

	private static final long serialVersionUID = -3293122515327864483L;

	@FieldDescribe("身份")
	private List<String> identityList = new ArrayList<>();

	@FieldDescribe("加入在指定位置之后,否则加入在指定位置之前.")
	private Boolean after;

	@FieldDescribe("是否执行替换")
	private Boolean replace;

	@FieldDescribe("工作标识")
	private String work;

	@FieldDescribe("定位身份标识")
	private String identity;

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

	public String getWork() {
		return work;
	}

	public void setWork(String work) {
		this.work = work;
	}

	public Boolean getAfter() {
		return BooleanUtils.isTrue(after);
	}

	public void setAfter(Boolean after) {
		this.after = after;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

}