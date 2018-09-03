package com.x.query.core.entity.plan;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.tools.DateTools;

public class DateRangeEntry extends GsonPropertyObject {

	
	public static final String DATERANGETYPE_YEAR = "year";
	public static final String DATERANGETYPE_SEASON = "season";
	public static final String DATERANGETYPE_MONTH = "month";
	public static final String DATERANGETYPE_WEEK = "week";
	public static final String DATERANGETYPE_DATE = "date";
	public static final String DATERANGETYPE_RANGE = "range";
	public static final String DATERANGETYPE_NONE = "none";

	public Boolean available() {
		if ((null == start) && (null == completed)) {
			return false;
		}
		return true;
	}

	public String year;
	public String month;
	public String date;
	public Integer season;
	public Integer week;
	public Integer adjust;

	public Date start;

	public Date completed;

	public String dateRangeType;

	public void adjust() throws Exception {
		Date now = new Date();
		if (null == this.adjust) {
			this.adjust = 0;
		}
		if (null == this.dateRangeType) {
			this.dateRangeType = DATERANGETYPE_NONE;
		}
		switch (this.dateRangeType) {
		case DATERANGETYPE_YEAR:
			if (StringUtils.isEmpty(year)) {
				year = DateTools.format(now, DateTools.format_yyyy);
			}
			this.start = DateTools.floorYear(year, adjust);
			this.completed = DateTools.ceilYear(year, adjust);
			break;
		case DATERANGETYPE_SEASON:
			if (StringUtils.isEmpty(year)) {
				year = DateTools.format(now, DateTools.format_yyyy);
			}
			if (null == season) {
				season = DateTools.season(now);
			}
			this.start = DateTools.floorSeason(year, season, adjust);
			this.completed = DateTools.ceilSeason(year, season, adjust);
			break;
		case DATERANGETYPE_MONTH:
			if (StringUtils.isEmpty(year)) {
				year = DateTools.format(now, DateTools.format_yyyy);
			}
			if (StringUtils.isEmpty(month)) {
				month = DateTools.format(now, DateTools.format_MM);
			}
			this.start = DateTools.floorMonth(year, month, adjust);
			this.completed = DateTools.ceilMonth(year, month, adjust);
			break;
		case DATERANGETYPE_WEEK:
			if (StringUtils.isEmpty(year)) {
				year = DateTools.format(now, DateTools.format_yyyy);
			}
			if (null == week) {
				week = DateTools.week(now);
			}
			this.start = DateTools.floorWeekOfYear(year, week, adjust);
			this.completed = DateTools.ceilWeekOfYear(year, week, adjust);
			break;
		case DATERANGETYPE_DATE:
			if (StringUtils.isEmpty(year)) {
				year = DateTools.format(now, DateTools.format_yyyy);
			}
			if (StringUtils.isEmpty(month)) {
				month = DateTools.format(now, DateTools.format_MM);
			}
			if (StringUtils.isEmpty(date)) {
				date = DateTools.format(now, DateTools.format_dd);
			}
			this.start = DateTools.floorDate(year, month, date, adjust);
			this.completed = DateTools.ceilDate(year, month, date, adjust);
			break;
		case DATERANGETYPE_RANGE:
			if (null == this.start || null == this.completed) {
				throw new Exception("begin or end can not be null when dateRangeEntry on type appoint.");
			}
			break;
		case DATERANGETYPE_NONE:
			this.completed = now;
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.YEAR, -1);
			this.start = cal.getTime();
			break;
		default:
			break;
		}
	}
}