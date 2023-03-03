package com.x.processplatform.core.express.service.processing.jaxrs.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.processplatform.core.entity.element.ActivityType;

public class WrapUpdatePrevTaskIdentity extends GsonPropertyObject {

	private static final long serialVersionUID = 6036782582457148615L;

	@FieldDescribe("上一人工环节处理人列表")
	private List<String> prevTaskIdentityList = new ArrayList<>();

	@FieldDescribe("上一人工环节处理人")
	private String prevTaskIdentity;

	@FieldDescribe("要更新的待办对象")
	private List<String> taskList;

	@FieldDescribe("上一人工环节处理的待办列表")
	private List<PrevTask> prevTaskList;

	@FieldDescribe("上一人工环节处理的最近一个待办")
	private PrevTask prevTask;

	public List<String> getPrevTaskIdentityList() {
		if (null == this.prevTaskIdentityList) {
			this.prevTaskIdentityList = new ArrayList<String>();
		}
		return prevTaskIdentityList;
	}

	public List<PrevTask> getPrevTaskList() {
		if (null == this.prevTaskList) {
			this.prevTaskList = new ArrayList<PrevTask>();
		}
		return prevTaskList;
	}

	public List<String> getTaskList() {
		if (null == this.taskList) {
			this.taskList = new ArrayList<String>();
		}
		return taskList;
	}

	public void setTaskList(List<String> taskList) {
		this.taskList = taskList;
	}

	public void setPrevTaskIdentityList(List<String> prevTaskIdentityList) {
		this.prevTaskIdentityList = prevTaskIdentityList;
	}

	public void setPrevTaskList(List<PrevTask> prevTaskList) {
		this.prevTaskList = prevTaskList;
	}

	public PrevTask getPrevTask() {
		return prevTask;
	}

	public void setPrevTask(PrevTask prevTask) {
		this.prevTask = prevTask;
	}

	public String getPrevTaskIdentity() {
		return prevTaskIdentity;
	}

	public void setPrevTaskIdentity(String prevTaskIdentity) {
		this.prevTaskIdentity = prevTaskIdentity;
	}

	public static class PrevTask extends GsonPropertyObject {

		private static final long serialVersionUID = -3547224997595040435L;

		private String identity;
		private String person;
		private String unit;
		private String routeName;
		private String opinion;
		private Date startTime;
		private Date completedTime;
		private String activity;
		private String activityName;
		private String activityToken;
		private ActivityType activityType;

		public ActivityType getActivityType() {
			return activityType;
		}

		public void setActivityType(ActivityType activityType) {
			this.activityType = activityType;
		}

		public String getActivity() {
			return activity;
		}

		public void setActivity(String activity) {
			this.activity = activity;
		}

		public String getActivityName() {
			return activityName;
		}

		public void setActivityName(String activityName) {
			this.activityName = activityName;
		}

		public String getActivityToken() {
			return activityToken;
		}

		public void setActivityToken(String activityToken) {
			this.activityToken = activityToken;
		}

		public String getIdentity() {
			return identity;
		}

		public void setIdentity(String identity) {
			this.identity = identity;
		}

		public String getOpinion() {
			return opinion;
		}

		public void setOpinion(String opinion) {
			this.opinion = opinion;
		}

		public Date getStartTime() {
			return startTime;
		}

		public void setStartTime(Date startTime) {
			this.startTime = startTime;
		}

		public Date getCompletedTime() {
			return completedTime;
		}

		public void setCompletedTime(Date completedTime) {
			this.completedTime = completedTime;
		}

		public String getPerson() {
			return person;
		}

		public void setPerson(String person) {
			this.person = person;
		}

		public String getUnit() {
			return unit;
		}

		public void setUnit(String unit) {
			this.unit = unit;
		}

		public String getRouteName() {
			return routeName;
		}

		public void setRouteName(String routeName) {
			this.routeName = routeName;
		}

	}

}