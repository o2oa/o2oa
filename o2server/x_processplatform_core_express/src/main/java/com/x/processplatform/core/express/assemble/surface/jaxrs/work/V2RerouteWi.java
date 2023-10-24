package com.x.processplatform.core.express.assemble.surface.jaxrs.work;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.processplatform.core.entity.element.ActivityType;

public class V2RerouteWi extends GsonPropertyObject {

	private static final long serialVersionUID = -6729193803512068864L;

	@FieldDescribe("目标节点.")
	private String activity;

	@FieldDescribe("节点类型.")
	private ActivityType activityType;

	@FieldDescribe("是否合并所有的work")
	private Boolean mergeWork;

	@FieldDescribe("人工活动强制处理人")
	private List<String> distinguishedNameList = new ArrayList<>();

	public Boolean getMergeWork() {
		return BooleanUtils.isTrue(mergeWork);
	}

	public List<String> getDistinguishedNameList() {
		if (null == distinguishedNameList) {
			this.distinguishedNameList = new ArrayList<>();
		}
		return this.distinguishedNameList;
	}

	public void setDistinguishedNameList(List<String> distinguishedNameList) {
		this.distinguishedNameList = distinguishedNameList;
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

	public ActivityType getActivityType() {
		return activityType;
	}

	public void setActivityType(ActivityType activityType) {
		this.activityType = activityType;
	}
	
	

}