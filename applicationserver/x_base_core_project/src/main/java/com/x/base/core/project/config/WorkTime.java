package com.x.base.core.project.config;

import com.x.base.core.project.gson.GsonPropertyObject;

public class WorkTime extends GsonPropertyObject {

	public static WorkTime defaultInstance() {
		WorkTime o = new WorkTime();
		return o;
	}

	public WorkTime() {
		this.amStart = "09:00:00";
		this.amEnd = "11:30:00";
		this.pmStart = "13:00:00";
		this.pmEnd = "17:30:00";
		this.weekends = new int[] { 1, 7 };
		this.holidays = new String[] {};
		this.workdays = new String[] {};
	}

	private String amStart;
	private String amEnd;
	private String pmStart;
	private String pmEnd;
	private String[] holidays;
	private String[] workdays;
	private int[] weekends;

	public String getAmStart() {
		return amStart;
	}

	public void setAmStart(String amStart) {
		this.amStart = amStart;
	}

	public String getAmEnd() {
		return amEnd;
	}

	public void setAmEnd(String amEnd) {
		this.amEnd = amEnd;
	}

	public String getPmStart() {
		return pmStart;
	}

	public void setPmStart(String pmStart) {
		this.pmStart = pmStart;
	}

	public String getPmEnd() {
		return pmEnd;
	}

	public void setPmEnd(String pmEnd) {
		this.pmEnd = pmEnd;
	}

	public String[] getHolidays() {
		return holidays;
	}

	public void setHolidays(String[] holidays) {
		this.holidays = holidays;
	}

	public String[] getWorkdays() {
		return workdays;
	}

	public void setWorkdays(String[] workdays) {
		this.workdays = workdays;
	}

	public int[] getWeekends() {
		return weekends;
	}

	public void setWeekends(int[] weekends) {
		this.weekends = weekends;
	}

}
