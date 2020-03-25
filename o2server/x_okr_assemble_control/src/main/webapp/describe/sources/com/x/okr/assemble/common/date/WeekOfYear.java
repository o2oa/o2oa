package com.x.okr.assemble.common.date;

import java.util.Date;

public class WeekOfYear {

	private Date startDate = null;
	
	private Date endDate = null;
	
	private String startDateString = null;
	
	private String endDateString = null;
	
	private Integer weekNo = 1;
	
	private Integer year = 1;

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getStartDateString() {
		return startDateString;
	}

	public void setStartDateString(String startDateString) {
		this.startDateString = startDateString;
	}

	public String getEndDateString() {
		return endDateString;
	}

	public void setEndDateString(String endDateString) {
		this.endDateString = endDateString;
	}

	public Integer getWeekNo() {
		return weekNo;
	}

	public void setWeekNo(Integer weekNo) {
		this.weekNo = weekNo;
	}
	
	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public String toString(){
		return this.year + "年第"+this.weekNo + "周:" + this.startDateString + " -- " + this.endDateString;
	}
}
