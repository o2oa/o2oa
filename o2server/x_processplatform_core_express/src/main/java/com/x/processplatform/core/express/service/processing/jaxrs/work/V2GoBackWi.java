package com.x.processplatform.core.express.service.processing.jaxrs.work;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class V2GoBackWi extends GsonPropertyObject {

	private static final long serialVersionUID = -6729193803512068864L;

	@FieldDescribe("活动节点")
	private String activity;

	@FieldDescribe("活动节点")
	private String activityToken;

	@FieldDescribe("路由方式")
	private String way;

	@FieldDescribe("人工活动强制处理人.")
	private List<String> distinguishedNameList = new ArrayList<>();

	public String getActivityToken() {
		return activityToken;
	}

	public void setActivityToken(String activityToken) {
		this.activityToken = activityToken;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public String getWay() {
		return way;
	}

	public void setWay(String way) {
		this.way = way;
	}

	public List<String> getDistinguishedNameList() {
		return distinguishedNameList;
	}

	public void setDistinguishedNameList(List<String> distinguishedNameList) {
		this.distinguishedNameList = distinguishedNameList;
	}

}