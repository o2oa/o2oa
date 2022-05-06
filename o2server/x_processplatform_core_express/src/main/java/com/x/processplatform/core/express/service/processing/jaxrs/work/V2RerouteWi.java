package com.x.processplatform.core.express.service.processing.jaxrs.work;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class V2RerouteWi extends GsonPropertyObject {

	private static final long serialVersionUID = -6729193803512068864L;

	@FieldDescribe("活动节点")
	private String activity;

	@FieldDescribe("节点类型")
	private String activityType;

	@FieldDescribe("是否合并所有的work")
	private Boolean mergeWork;

	@FieldDescribe("人工活动强制处理人")
	private List<String> manualForceTaskIdentityList = new ArrayList<>();

	public List<String> getManualForceTaskIdentityList() {
		if (null == this.manualForceTaskIdentityList) {
			this.manualForceTaskIdentityList = new ArrayList<>();
		}
		return manualForceTaskIdentityList;
	}

	public Boolean getMergeWork() {
		return BooleanUtils.isTrue(mergeWork);
	}

	public void setManualForceTaskIdentityList(List<String> manualForceTaskIdentityList) {
		this.manualForceTaskIdentityList = manualForceTaskIdentityList;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public void setMergeWork(Boolean mergeWork) {
		this.mergeWork = mergeWork;
	}

	public String getActivityType() {
		return activityType;
	}

	public void setActivityType(String activityType) {
		this.activityType = activityType;
	}

}