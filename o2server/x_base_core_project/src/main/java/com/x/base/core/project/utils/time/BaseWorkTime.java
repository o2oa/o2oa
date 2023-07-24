package com.x.base.core.project.utils.time;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

/**
 * ----i1-------i2-------i3-------i4-------i5-----
 * ***********************************************
 * t1-------t2-------t3-------t4-------t5-------t6
 * 
 */
public class BaseWorkTime {
	protected static final String[] DATEPARTFORMATPATTERN = { "yyyy-MM-dd" };
	protected static final String[] TIMEPARTFORMATPATTERN = { "HH:mm:ss" };
	private long t1;
	private long t2;
	private long t3;
	private long t4;
	private long t5;
	private long t6;
	private long t2t1;
	private long t3t2;
	private long t4t3;
	private long t5t4;
	private long t6t5;
	private String[] definedHolidays;
	private String[] definedWorkdays;
	private int[] definedWeekends;

	/**
	 * @param forenoonStart
	 * @param forenoonEnd
	 * @param afternoonStart
	 * @param afternoonEnd
	 * @param definedHolidays
	 * @param definedWorkdays
	 * @param definedWeekends
	 */
	protected BaseWorkTime(String forenoonStart, String forenoonEnd, String afternoonStart, String afternoonEnd,
			String[] definedHolidays, String[] definedWorkdays, int[] definedWeekends) {
		try {
			this.t1 = 0;
			this.t2 = DateUtils.getFragmentInMilliseconds(DateUtils.parseDate(forenoonStart, TIMEPARTFORMATPATTERN),
					Calendar.DATE);
			this.t3 = DateUtils.getFragmentInMilliseconds(DateUtils.parseDate(forenoonEnd, TIMEPARTFORMATPATTERN),
					Calendar.DATE);
			this.t4 = DateUtils.getFragmentInMilliseconds(DateUtils.parseDate(afternoonStart, TIMEPARTFORMATPATTERN),
					Calendar.DATE);
			this.t5 = DateUtils.getFragmentInMilliseconds(DateUtils.parseDate(afternoonEnd, TIMEPARTFORMATPATTERN),
					Calendar.DATE);
			this.t6 = 86400000;

			if (t2 < t1)
				t2 = 0;
			if (t3 < t2)
				t3 = t2;
			if (t4 < t3)
				t4 = t3;
			if (this.t5 < 1)
				this.t5 = 86400000;
			if (t5 < t4)
				t5 = t4;

			this.t2t1 = t2 - t1;
			this.t3t2 = t3 - t2;
			this.t4t3 = t4 - t3;
			this.t5t4 = t5 - t4;
			this.t6t5 = t6 - t5;
			if (!ArrayUtils.isEmpty(definedHolidays)) {
				Set<String> h = new HashSet<String>();
				for (String s : definedHolidays) {
					h.add(DateFormatUtils.format(DateUtils.parseDate(s, DATEPARTFORMATPATTERN),
							DATEPARTFORMATPATTERN[0]));
				}
				this.definedHolidays = h.toArray(new String[] {});
			}
			if (!ArrayUtils.isEmpty(definedWorkdays)) {
				Set<String> w = new HashSet<String>();
				for (String s : definedWorkdays) {
					w.add(DateFormatUtils.format(DateUtils.parseDate(s, DATEPARTFORMATPATTERN),
							DATEPARTFORMATPATTERN[0]));
				}
				this.definedWorkdays = w.toArray(new String[] {});
			}
			if (ArrayUtils.isEmpty(definedWeekends)) {
				this.definedWeekends = new int[] { Calendar.SUNDAY, Calendar.SATURDAY };
			} else {
				this.definedWeekends = definedWeekends;
			}
		} catch (ParseException e) {
			e.printStackTrace(System.out);
		}
	}

	protected long workDaysBetween(Calendar s, Calendar e) {
		long i = 0;
		if (s.before(e)) {
			Calendar sx = DateUtils.toCalendar(s.getTime());
			Calendar ex = DateUtils.toCalendar(e.getTime());
			while (!DateUtils.isSameDay(sx, ex)) {
				sx.add(Calendar.DATE, 1);
				if (!this.isHoliday(sx)) {
					i++;
				}
			}
		}
		return i;
	}

	protected long workMilliSecondsBetweenInSameDay(Calendar s, Calendar e) throws Exception {
		long i = 0;
		long sm = DateUtils.getFragmentInMilliseconds(s, Calendar.DATE);
		long em = DateUtils.getFragmentInMilliseconds(e, Calendar.DATE);
		switch (inRegion(s) - inRegion(e)) {
		case 0:
			i = em - sm;
			break;
		case -2:
			i = (em - t4) + (t3 - sm);
			break;
		case 2:
			i = -((t3 - em) + (sm - t4));
			break;
		}
		return i;
	}

	protected Calendar forwardMilliSeconds(Calendar c, long milliSeconds) {
		Calendar f = DateUtils.toCalendar(c.getTime());
		long l = milliSeconds % this.workMilliSecondsOfDay();
		long fm = DateUtils.getFragmentInMilliseconds(f, Calendar.DATE);
		switch (inRegion(f)) {
		case 2:
			if (l - t5t4 - (t3 - fm) >= 0) {
				f.add(Calendar.MILLISECOND, (int) (l + t4t3 + t6t5 + t2t1));
			} else if (l - (t3 - fm) >= 0) {
				f.add(Calendar.MILLISECOND, (int) (l + t4t3));
			} else {
				f.add(Calendar.MILLISECOND, (int) (l));
			}
			break;
		case 4:
			if ((l - (t5 - fm) - t3t2) >= 0) {
				f.add(Calendar.MILLISECOND, (int) (l + t6t5 + t2t1 + t4t3));
			} else if ((l - (t5 - fm)) >= 0) {
				f.add(Calendar.MILLISECOND, (int) (l + t6t5 + t2t1));
			} else {
				f.add(Calendar.MILLISECOND, (int) (l));
			}
			break;
		}
		while (this.isHoliday(f)) {
			f.add(Calendar.DATE, 1);
		}
		long day = (long) Math.floor(milliSeconds / this.workMilliSecondsOfDay());
		while (day > 0) {
			f.add(Calendar.DATE, 1);
			if (!this.isHoliday(f)) {
				day--;
			}
		}
		return f;
	}

	protected int inRegion(Calendar c) {
		int i = 5;
		long m = DateUtils.getFragmentInMilliseconds(c, Calendar.DATE);
		if (m < t2) {
			i = 1;
		} else if (m <= t3) {
			i = 2;
		} else if (m < t4) {
			i = 3;
		} else if (m <= t5) {
			i = 4;
		}
		return i;
	}

	protected boolean isHoliday(Calendar c) {
		if (this.inDefinedHoliday(c)) {
			return true;
		}
		if (this.inDefinedWorkday(c)) {
			return false;
		}
		if (this.inDefinedWeekends(c)) {
			return true;
		}
		return false;
	}

	public boolean inDefinedHoliday(Date d) {
		return this.inDefinedHoliday(DateUtils.toCalendar(d));
	}

	public boolean inDefinedHoliday(Calendar c) {
		if (ArrayUtils.isNotEmpty(this.definedHolidays)) {
			if (ArrayUtils.indexOf(this.definedHolidays,
					DateFormatUtils.format(c, DATEPARTFORMATPATTERN[0])) > ArrayUtils.INDEX_NOT_FOUND) {
				return true;
			}
		}
		return false;
	}

	public boolean inDefinedWorkday(Date d) {
		return this.inDefinedWorkday(DateUtils.toCalendar(d));
	}

	public boolean inDefinedWorkday(Calendar c) {
		if (ArrayUtils.isNotEmpty(this.definedWorkdays)) {
			if (ArrayUtils.indexOf(this.definedWorkdays,
					DateFormatUtils.format(c, DATEPARTFORMATPATTERN[0])) > ArrayUtils.INDEX_NOT_FOUND) {
				return true;
			}
		}
		return false;
	}

	private boolean inDefinedWeekends(Calendar c) {
		if (ArrayUtils.indexOf(this.definedWeekends, c.get(Calendar.DAY_OF_WEEK)) > ArrayUtils.INDEX_NOT_FOUND) {
			return true;
		}
		return false;
	}

	protected long workMilliSecondsOfDay() {
		return t5t4 + t3t2;
	}

	protected long workMilliSecondsOfForenoon() {
		return t3t2;
	}

	protected long workMilliSecondsOfAfternoon() {
		return t5t4;
	}

	protected Calendar forwardStartOfWorkday(Calendar c) {
		Calendar cx = DateUtils.toCalendar(c.getTime());
		if (this.isHoliday(cx)) {
			while (this.isHoliday(cx)) {
				cx.add(Calendar.DATE, 1);
			}
			cx.add(Calendar.MILLISECOND, (int) (t2 - DateUtils.getFragmentInMilliseconds(cx, Calendar.DATE)));
		}
		return cx;
	}

	protected Calendar backwardEndOfWorkday(Calendar c) {
		Calendar cx = DateUtils.toCalendar(c.getTime());
		if (this.isHoliday(cx)) {
			while (this.isHoliday(cx)) {
				cx.add(Calendar.DATE, -1);
			}
			cx.add(Calendar.MILLISECOND, (int) (t5 - DateUtils.getFragmentInMilliseconds(cx, Calendar.DATE)));
		}
		return cx;
	}

	protected Calendar forwardStartOfRegion(Calendar c) {
		Calendar cx = DateUtils.toCalendar(c.getTime());
		long cm = DateUtils.getFragmentInMilliseconds(cx, Calendar.DATE);
		switch (inRegion(cx)) {
		case 1:
			cx.add(Calendar.MILLISECOND, (int) (t2 - cm));
			break;
		case 3:
			cx.add(Calendar.MILLISECOND, (int) (t4 - cm));
			break;
		case 5:
			cx.add(Calendar.MILLISECOND, (int) ((t6 - cm) + t2t1));
			break;
		}
		return cx;
	}

	protected Calendar backwardEndOfRegion(Calendar c) {
		Calendar cx = DateUtils.toCalendar(c.getTime());
		long cm = DateUtils.getFragmentInMilliseconds(cx, Calendar.DATE);
		switch (inRegion(cx)) {
		case 1:
			cx.add(Calendar.MILLISECOND, -(int) ((cm - t1) + t6t5));
			break;
		case 3:
			cx.add(Calendar.MILLISECOND, -(int) (cm - t3));
			break;
		case 5:
			cx.add(Calendar.MILLISECOND, -(int) (cm - t5));
			break;
		}
		return cx;
	}

	public Integer minutesOfWorkDay() throws Exception {
		return (int) (this.workMilliSecondsOfDay() / (60 * 1000));
	}
}
