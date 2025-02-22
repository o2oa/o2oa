package com.x.processplatform.core.express.service.processing.jaxrs.work;

import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class V3RetractWi extends GsonPropertyObject {

	private static final long serialVersionUID = 5176027793859273796L;

	@FieldDescribe("工作标识")
	private String work;

	@FieldDescribe("已办标识")
	private String taskCompleted;

	@FieldDescribe("撤回工作标识")
	private List<String> retractWorkList;

	@FieldDescribe("撤回待办标识")
	private List<String> retractTaskList;

	public String getWork() {
		return work;
	}

	public void setWork(String work) {
		this.work = work;
	}

	public String getTaskCompleted() {
		return taskCompleted;
	}

	public void setTaskCompleted(String taskCompleted) {
		this.taskCompleted = taskCompleted;
	}

	public List<String> getRetractWorkList() {
		return retractWorkList;
	}

	public void setRetractWorkList(List<String> retractWorkList) {
		this.retractWorkList = retractWorkList;
	}

	public List<String> getRetractTaskList() {
		return retractTaskList;
	}

	public void setRetractTaskList(List<String> retractTaskList) {
		this.retractTaskList = retractTaskList;
	}

}
