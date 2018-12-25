package com.x.base.core.project.schedule;

import org.quartz.Job;

import com.x.base.core.project.gson.GsonPropertyObject;

public class ScheduleRequest extends GsonPropertyObject {

	public ScheduleRequest() {
	}

	public <T extends Job> ScheduleRequest(Class<T> cls, String application, String node, String cron) {
		this.className = cls.getName();
		this.application = application;
		this.node = node;
		this.cron = cron;
	}

	private String className;
	private String application;
	private String node;
	private String cron;

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getNode() {
		return node;
	}

	public void setNode(String node) {
		this.node = node;
	}

	public String getCron() {
		return cron;
	}

	public void setCron(String cron) {
		this.cron = cron;
	}

}
