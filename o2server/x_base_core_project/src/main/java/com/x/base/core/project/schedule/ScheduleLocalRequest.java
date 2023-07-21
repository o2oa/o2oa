package com.x.base.core.project.schedule;

import org.quartz.JobDetail;

import com.x.base.core.project.gson.GsonPropertyObject;

public class ScheduleLocalRequest extends GsonPropertyObject {

	public ScheduleLocalRequest() {
	}

	public ScheduleLocalRequest(JobDetail jobDetail, String cron, Integer delay, Integer interval) {
		this.className = jobDetail.getKey().getName();
		this.cron = cron;
		this.delay = delay;
		this.interval = interval;
	}

	private String className;
	private String cron;
	private Integer delay;
	private Integer interval;

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getCron() {
		return cron;
	}

	public void setCron(String cron) {
		this.cron = cron;
	}

	public Integer getDelay() {
		return delay;
	}

	public void setDelay(Integer delay) {
		this.delay = delay;
	}

	public Integer getInterval() {
		return interval;
	}

	public void setInterval(Integer interval) {
		this.interval = interval;
	}

}
