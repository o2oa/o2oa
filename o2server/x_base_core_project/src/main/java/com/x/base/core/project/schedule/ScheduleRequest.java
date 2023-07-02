package com.x.base.core.project.schedule;

import java.util.Date;

import org.quartz.Job;

import com.x.base.core.project.gson.GsonPropertyObject;

public class ScheduleRequest extends GsonPropertyObject {

	public ScheduleRequest() {
	}

	public <T extends Job> ScheduleRequest(Class<T> cls, String node, String cron) {
		this.className = cls.getName();
		this.cron = cron;
	}

	private String className;
	private String cron;
	private Date lastStartTime;

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public Date getLastStartTime() {
		return lastStartTime;
	}

	public void setLastStartTime(Date lastStartTime) {
		this.lastStartTime = lastStartTime;
	}

	public String getCron() {
		return cron;
	}

	public void setCron(String cron) {
		this.cron = cron;
	}

}
