package com.x.processplatform.core.express.service.processing.jaxrs.task;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.gson.GsonPropertyObject;

public class WrapUpdatePrevTaskIdentity extends GsonPropertyObject {

	private List<String> prevTaskIdentityList = new ArrayList<>();

	private List<String> taskList;

	public List<String> getPrevTaskIdentityList() {
		if (null == this.prevTaskIdentityList) {
			this.prevTaskIdentityList = new ArrayList<String>();
		}
		return prevTaskIdentityList;
	}

	public void setPrevTaskIdentityList(List<String> prevTaskIdentityList) {
		this.prevTaskIdentityList = prevTaskIdentityList;
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

}