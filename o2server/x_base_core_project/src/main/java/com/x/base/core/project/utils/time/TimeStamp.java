package com.x.base.core.project.utils.time;

import java.util.Date;

public class TimeStamp {

	private Date start;
	private Date last;

	public TimeStamp() {
		start = new Date();
		last = start;
	}

	public String stampSeconds() {
		Date date = new Date();
		String value = ((date.getTime() - last.getTime()) / 1000) + "sec";
		last = date;
		return value;
	}

	public String stampMilliseconds() {
		Date date = new Date();
		String value = (date.getTime() - last.getTime()) + "ms";
		last = date;
		return value;
	}

	public String consumingSeconds() {
		Date date = new Date();
		return ((date.getTime() - start.getTime()) / 1000) + "sec";
	}

	public String consumingMilliseconds() {
		Date date = new Date();
		return (date.getTime() - start.getTime()) + "ms";
	}

}
