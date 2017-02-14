package com.x.common.core.application.definition;

import com.x.base.core.utils.time.WorkTime;

public class WorkTimeDefinition extends LoadableDefinition {

	public static WorkTimeDefinition INSTANCE;

	public static final String FILE_NAME = "workTimeDefinition.json";

	public void initWorkTime() {
		WorkTime.AM_START = getAmStart();
		WorkTime.AM_END = getAmEnd();
		WorkTime.PM_START = getPmStart();
		WorkTime.PM_END = getPmEnd();
		WorkTime.WEEKENDS = getWeekends();
		WorkTime.HOLIDAYS = getHolidays();
		WorkTime.WORKDAYS = getWorkdays();
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
