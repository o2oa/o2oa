package com.x.processplatform.core.express.service.processing.jaxrs.taskcompleted;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.gson.GsonPropertyObject;

public class WrapUpdateNextTaskIdentity extends GsonPropertyObject {

	private List<String> nextTaskIdentityList = new ArrayList<>();

	private List<String> taskCompletedList = new ArrayList<>();

	public List<String> getNextTaskIdentityList() {
		if (null == this.nextTaskIdentityList) {
			this.nextTaskIdentityList = new ArrayList<String>();
		}
		return nextTaskIdentityList;
	}

	public void setNextTaskIdentityList(List<String> nextTaskIdentityList) {
		this.nextTaskIdentityList = nextTaskIdentityList;
	}

	public List<String> getTaskCompletedList() {
		if (null == this.taskCompletedList) {
			this.taskCompletedList = new ArrayList<String>();
		}
		return taskCompletedList;
	}

	public void setTaskCompletedList(List<String> taskCompletedList) {
		this.taskCompletedList = taskCompletedList;
	}

}