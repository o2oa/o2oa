package com.x.processplatform.core.express.service.processing.jaxrs.taskcompleted;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class WrapUpdateNextTaskIdentity extends GsonPropertyObject {

	private static final long serialVersionUID = -597948505960097189L;

	@FieldDescribe("后续环节待办人")
	private List<String> nextTaskIdentityList = new ArrayList<>();

	@FieldDescribe("已办标识")
	private List<String> taskCompletedList = new ArrayList<>();

	public List<String> getNextTaskIdentityList() {
		if (null == this.nextTaskIdentityList) {
			this.nextTaskIdentityList = new ArrayList<>();
		}
		return nextTaskIdentityList;
	}

	public void setNextTaskIdentityList(List<String> nextTaskIdentityList) {
		this.nextTaskIdentityList = nextTaskIdentityList;
	}

	public List<String> getTaskCompletedList() {
		if (null == this.taskCompletedList) {
			this.taskCompletedList = new ArrayList<>();
		}
		return taskCompletedList;
	}

	public void setTaskCompletedList(List<String> taskCompletedList) {
		this.taskCompletedList = taskCompletedList;
	}

}