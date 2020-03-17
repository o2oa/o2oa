package com.x.attendance.assemble.control.jaxrs;

import java.io.Serializable;

import com.x.base.core.project.gson.GsonPropertyObject;

public class DateRecord extends GsonPropertyObject implements Serializable{
	private static final long serialVersionUID = 1L;
	private String year;
	private String month;
	private String date;
	public DateRecord() {
		
	}
	public DateRecord(String recordYearString, String recordMonthString) {
		this.year = recordYearString;
		this.month = recordMonthString;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
	
}
