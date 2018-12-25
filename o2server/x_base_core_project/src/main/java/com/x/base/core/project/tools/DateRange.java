package com.x.base.core.project.tools;

import java.util.Date;

public class DateRange {

	public DateRange(Date start, Date end) {
		this.start = start;
		this.end = end;
	}

	private Date start;

	private Date end;

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

}