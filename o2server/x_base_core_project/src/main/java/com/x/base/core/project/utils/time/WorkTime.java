package com.x.base.core.project.utils.time;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;

public class WorkTime extends BaseWorkTime {

	public static volatile String AM_START = "09:00:00";
	public static volatile String AM_END = "11:30:00";
	public static volatile String PM_START = "13:00:00";
	public static volatile String PM_END = "17:30:00";

	public static volatile String[] HOLIDAYS = new String[] {};

	public static volatile String[] WORKDAYS = new String[] {};

	public static volatile int[] WEEKENDS = new int[] { 1, 7 };

	private WorkTime() {
		super(AM_START, AM_END, PM_START, PM_END, HOLIDAYS, WORKDAYS, WEEKENDS);
	}

	public WorkTime(String amStart, String amEnd, String pmStart, String pmEnd) {
		super(amStart, amEnd, pmStart, pmEnd, null, null, null);
	}

	public WorkTime(String amStart, String amEnd, String pmStart, String pmEnd, String[] definedHolidays) {
		super(amStart, amEnd, pmStart, pmEnd, definedHolidays, null, null);
	}

	public WorkTime(String amStart, String amEnd, String pmStart, String pmEnd, String[] definedHolidays,
			String[] definedWorkdays) {
		super(amStart, amEnd, pmStart, pmEnd, definedHolidays, definedWorkdays, null);
	}

	public WorkTime(String amStart, String amEnd, String pmStart, String pmEnd, String[] definedHolidays,
			String[] definedWorkdays, int[] definedWeekends) {
		super(amStart, amEnd, pmStart, pmEnd, definedHolidays, definedWorkdays, definedWeekends);
	}

	public long betweenMinutes(Date s, Date e) {
		return this.betweenMinutes(DateUtils.toCalendar(s), DateUtils.toCalendar(e));
	}

	public long betweenMinutes(Calendar s, Calendar e) {
		Calendar sx = this.forwardStartOfWorkday(this.forwardStartOfRegion(s));
		Calendar ex = this.backwardEndOfWorkday(this.backwardEndOfRegion(e));
		if (ex.after(sx)) {
			long i = 0;
			try {
				long m = this.workMilliSecondsBetweenInSameDay(sx, ex);
				long d = this.workDaysBetween(sx, ex);
				i = m + (d * this.workMilliSecondsOfDay());
			} catch (Exception exception) {
				exception.printStackTrace(System.out);
			}
			return (long) i / 60000;
		} else {
			return 0;
		}
	}

	public Date forwardMinutes(Date d, long mintues) {
		return this.forwardMinutes(DateUtils.toCalendar(d), mintues).getTime();
	}

	public Calendar forwardMinutes(Calendar c, long mintues) {
		Calendar cx = this.forwardStartOfWorkday(this.forwardStartOfRegion(c));
		return this.forwardMilliSeconds(cx, mintues * 60000);
	}

	public boolean isWorkTime(Date d) {
		return this.isWorkTime(DateUtils.toCalendar(d));
	}

	public boolean isWorkTime(Calendar c) {
		if (!this.isHoliday(c)) {
			int i = this.inRegion(c);
			if (i == 2 || i == 4) {
				return true;
			}
		}
		return false;
	}

	public boolean isWorkDay(Date d) {
		return !this.isHoliday(DateUtils.toCalendar(d));
	}

	public boolean isWorkDay(Calendar c) {
		return !this.isHoliday(c);
	}

}
