package com.x.base.core.project.utils.time;

import java.util.Date;

public class TimeStamp {

	private Date start;

	public TimeStamp() {
		start = new Date();
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
