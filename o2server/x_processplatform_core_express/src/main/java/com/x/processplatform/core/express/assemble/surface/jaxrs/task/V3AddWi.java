package com.x.processplatform.core.express.assemble.surface.jaxrs.task;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class V3AddWi extends GsonPropertyObject {

	private static final long serialVersionUID = -7183537361715762746L;

	private static final String MODE_SINGLE = "single";
	private static final String MODE_QUEUE = "queue";
	private static final String MODE_PARALLEL = "parallel";

	@FieldDescribe("加签模式:single,queue,parallel.")
	private String mode;

	@FieldDescribe("是否是前加签,false后加签.")
	private Boolean before;

	@FieldDescribe("加签人员.")
	private List<String> distinguishedNameList;

	@FieldDescribe("路由名称")
	private String routeName;

	@FieldDescribe("意见")
	private String opinion;

	public String getMode() {
		if (StringUtils.equalsIgnoreCase(mode, MODE_QUEUE)) {
			return MODE_QUEUE;
		} else if (StringUtils.equalsIgnoreCase(mode, MODE_PARALLEL)) {
			return MODE_PARALLEL;
		} else {
			return MODE_SINGLE;
		}
	}

	public List<String> getDistinguishedNameList() {
		return null == this.distinguishedNameList ? new ArrayList<>() : this.distinguishedNameList;
	}

	public String getRouteName() {
		return routeName;
	}

	public String getOpinion() {
		return opinion;
	}

	public Boolean getBefore() {
		return BooleanUtils.isNotFalse(this.before);
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public void setBefore(Boolean before) {
		this.before = before;
	}

	public void setDistinguishedNameList(List<String> distinguishedNameList) {
		this.distinguishedNameList = distinguishedNameList;
	}

	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}

	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}

}
