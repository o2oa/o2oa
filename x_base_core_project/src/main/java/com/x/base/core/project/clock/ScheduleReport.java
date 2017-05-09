package com.x.base.core.project.clock;

import java.util.Date;

public class ScheduleReport {

	public ScheduleReport() {
	}

	public ScheduleReport(String node, String application, String clockTaskClassName, Date start, Date end,
			Long interval) {
		this.node = node;
		this.application = application;
		this.clockTaskClassName = clockTaskClassName;
		this.start = start;
		this.end = end;
		this.interval = interval;
	}

	private String node;
	private String application;
	private String clockTaskClassName;
	private Date start;
	private Date end;
	private Long interval;

	public String getNode() {
		return node;
	}

	public void setNode(String node) {
		this.node = node;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getClockTaskClassName() {
		return clockTaskClassName;
	}

	public void setClockTaskClassName(String clockTaskClassName) {
		this.clockTaskClassName = clockTaskClassName;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public Long getInterval() {
		return interval;
	}

	public void setInterval(Long interval) {
		this.interval = interval;
	}
}
