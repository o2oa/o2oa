package com.x.cms.core.entity.query;

import java.util.Date;
import java.util.Objects;

import com.x.base.core.project.gson.GsonPropertyObject;

public class DateRangeEntry extends GsonPropertyObject {

	public DateRangeEntry() {
		this.dateRangeType = DateRangeType.none;
		this.dateEffectType = DateEffectType.publish;
		this.start = new Date();
		this.completed = new Date();
		this.year = "";
		this.month = "";
		this.date = "";
		this.season = 0;
		this.week = 0;
		this.adjust = 0;
	}

	public Boolean available() {
		if (null == this.getDateRangeType() || Objects.equals(this.dateRangeType, DateRangeType.none)) {
			return false;
		}
		if (null == this.dateEffectType) {
			return false;
		}
		if (null == start) {
			return false;
		}
		if (null == completed) {
			return false;
		}
		return true;
	}

	private DateRangeType dateRangeType;
	private DateEffectType dateEffectType;

	private Date start;
	private Date completed;

	private String year;
	private String month;
	private String date;
	private Integer season;
	private Integer week;
	private Integer adjust;

	public DateRangeType getDateRangeType() {
		return dateRangeType;
	}

	public void setDateRangeType(DateRangeType dateRangeType) {
		this.dateRangeType = dateRangeType;
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

	public Integer getSeason() {
		return season;
	}

	public void setSeason(Integer season) {
		this.season = season;
	}

	public Integer getWeek() {
		return week;
	}

	public void setWeek(Integer week) {
		this.week = week;
	}

	public Integer getAdjust() {
		return adjust;
	}

	public void setAdjust(Integer adjust) {
		this.adjust = adjust;
	}

	public DateEffectType getDateEffectType() {
		return dateEffectType;
	}

	public void setDateEffectType(DateEffectType dateEffectType) {
		this.dateEffectType = dateEffectType;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getCompleted() {
		return completed;
	}

	public void setCompleted(Date completed) {
		this.completed = completed;
	}

}
