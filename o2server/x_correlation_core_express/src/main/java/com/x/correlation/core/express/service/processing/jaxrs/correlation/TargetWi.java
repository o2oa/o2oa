package com.x.correlation.core.express.service.processing.jaxrs.correlation;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class TargetWi extends GsonPropertyObject {

	private static final long serialVersionUID = 395825437810549953L;

	@FieldDescribe("关联目标类型.")
	private String type;

	@FieldDescribe("关联目标标识.")
	private String bundle;

	@FieldDescribe("关联内容框标识.")
	private String site;

	@FieldDescribe("来源视图.")
	private String view;

	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getBundle() {
		return bundle;
	}

	public void setBundle(String bundle) {
		this.bundle = bundle;
	}

}