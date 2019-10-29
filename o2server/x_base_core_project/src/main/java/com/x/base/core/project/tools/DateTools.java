package com.x.base.core.project.tools;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.quartz.CronExpression;

public class DateTools {

	public final static String pattern_yyyyMMddHHmmss = "^[1,2][0,9][0-9][0-9]-[0,1]?[0-9]-[0-3]?[0-9] [0-5]?[0-9]:[0-5]?[0-9]:[0-5]?[0-9]$";
	public final static String pattern_yyyyMMdd = "^[1,2][0,9][0-9][0-9]-[0,1]?[0-9]-[0-3]?[0-9]$";
	public final static String pattern_HHmmss = "^[0-5]?[0-9]:[0-5]?[0-9]:[0-5]?[0-9]$";

	public final static String patternCompact_yyyyMMddHHmmss = "^[1,2][0,9][0-9][0-9][0,1][0-9][0-3][0-9][0-5][0-9][0-5][0-9][0-5][0-9]$";

	public final static String format_yyyy = "yyyy";
	public final static String format_MM = "MM";
	public final static String format_dd = "dd";
	public final static String format_yyyyMM = "yyyy-MM";
	public final static String format_yyyyMMdd = "yyyy-MM-dd";
	public final static String format_HHmmss = "HH:mm:ss";
	public final static String format_HHmm = "HH:mm";
	public final static String format_yyyyMMddHHmmss = "yyyy-MM-dd HH:mm:ss";
	public final static String format_yyyyMMddHHmm = "yyyy-MM-dd HH:mm";

	public final static String formatCompact_yyyyMMdd = "yyyyMMdd";
	public final static String formatCompact_yyyyMM = "yyyyMM";
	public final static String formatCompact_HHmmss = "HHmmss";
	public final static String formatCompact_HHmm = "HHmm";
	public final static String formatCompact_yyyyMMddHHmmss = "yyyyMMddHHmmss";

	public static boolean isFormat(String str, String format) {
		if (StringUtils.isEmpty(str)) {
			return false;
		}
		return Pattern.compile(format).matcher(str).find();
	}

	public static String format(Date date) {
		return DateFormatUtils.format(date, format_yyyyMMddHHmmss);
	}

	public static String formatDate(Date date) {
		return DateFormatUtils.format(date, format_yyyyMMdd);
	}

	public static String formatTime(Date date) {
		return DateFormatUtils.format(date, format_HHmmss);
	}

	public static String format(Date date, String format) {
		return DateFormatUtils.format(date, format);
	}

	public static String now() {
		return DateFormatUtils.format(new Date(), format_yyyyMMddHHmmss);
	}

	public static String compact(Date date) {
		return DateFormatUtils.format(date, formatCompact_yyyyMMddHHmmss);
	}

	public static String compactDate(Date date) {
		return DateFormatUtils.format(date, formatCompact_yyyyMMdd);
	}

	public static String compactTime(Date date) {
		return DateFormatUtils.format(date, formatCompact_HHmmss);
	}

	public static Boolean isDateTime(String str) {
		if (StringUtils.isEmpty(str)) {
			return Boolean.FALSE;
		}
		return Pattern.compile(pattern_yyyyMMddHHmmss).matcher(str).find();
	}

	public static Boolean isCompactDateTime(String str) {
		if (StringUtils.isEmpty(str)) {
			return Boolean.FALSE;
		}
		return Pattern.compile(patternCompact_yyyyMMddHHmmss).matcher(str).find();
	}

	public static Boolean isDate(String str) {
		if (StringUtils.isEmpty(str)) {
			return Boolean.FALSE;
		}
		return Pattern.compile(pattern_yyyyMMdd).matcher(str).find();
	}

	public static Boolean isTime(String str) {
		if (StringUtils.isEmpty(str)) {
			return Boolean.FALSE;
		}
		return Pattern.compile(pattern_HHmmss).matcher(str).find();
	}

	public static Boolean isDateTimeOrDateOrTime(String str) {
		return isDateTime(str) || isDate(str) || isTime(str);
	}

	public static Boolean isDateTimeOrDate(String str) {
		return isDateTime(str) || isDate(str);
	}

	public static Boolean isDateTimeOrTime(String str) {
		return isDateTime(str) || isTime(str);
	}

	public static Date parse(String str, String format) throws Exception {
		return DateUtils.parseDate(str, format);
	}

	public static Date parse(String str) throws Exception {
		return DateUtils.parseDate(str, new String[] { format_yyyyMMddHHmmss, format_yyyyMMdd, format_HHmmss });
	}

	public static Date parseDateTime(String str) throws Exception {
		if (isDateTime(str) || isCompactDateTime(str)) {
			return DateUtils.parseDate(str, new String[] { format_yyyyMMddHHmmss, formatCompact_yyyyMMddHHmmss });
		} else {
			return null;
		}
	}

	public static Date parseDate(String str) throws Exception {
		if (isDate(str)) {
			return DateUtils.parseDate(str, new String[] { format_yyyyMMdd, formatCompact_yyyyMMdd });
		} else {
			return null;
		}
	}

	public static Integer season(Date date) throws Exception {
		Calendar cal = DateUtils.toCalendar(date);
		switch (cal.get(Calendar.MONTH)) {
		case Calendar.JANUARY:
		case Calendar.FEBRUARY:
		case Calendar.MARCH:
			return 1;
		case Calendar.APRIL:
		case Calendar.MAY:
		case Calendar.JUNE:
			return 2;
		case Calendar.JULY:
		case Calendar.AUGUST:
		case Calendar.SEPTEMBER:
			return 3;
		case Calendar.OCTOBER:
		case Calendar.NOVEMBER:
		case Calendar.DECEMBER:
		default:
			return 4;
		}
	}

	public static Integer week(Date date) throws Exception {
		Calendar cal = DateUtils.toCalendar(date);
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		return cal.get(Calendar.WEEK_OF_YEAR);
	}

	public static Date parseTime(String str) throws Exception {
		if (isTime(str)) {
			return DateUtils.parseDate(str, new String[] { format_HHmmss, formatCompact_HHmmss });
		} else {
			return null;
		}
	}

	public static Date floorYear(String year, Integer adjust) throws Exception {
		Date date = parse(year, format_yyyy);
		return floorYear(date, adjust);
	}

	public static Date floorYear(Date date, Integer adjust) throws Exception {
		Calendar cal = DateUtils.toCalendar(date);
		if ((null != adjust) && (adjust != 0)) {
			cal.add(Calendar.YEAR, adjust);
		}
		cal.set(Calendar.MONTH, Calendar.JANUARY);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	public static Date ceilYear(String year, Integer adjust) throws Exception {
		Date date = parse(year, format_yyyy);
		return ceilYear(date, adjust);
	}

	public static Date ceilYear(Date date, Integer adjust) throws Exception {
		Calendar cal = DateUtils.toCalendar(date);
		if ((null != adjust) && (adjust != 0)) {
			cal.add(Calendar.YEAR, adjust);
		}
		return DateUtils.ceiling(cal, Calendar.YEAR).getTime();

	}

	public static Date floorWeekOfYear(String year, Integer weekOfYear, Integer adjust) throws Exception {
		Date date = parse(year, format_yyyy);
		Calendar cal = DateUtils.toCalendar(date);
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		cal.set(Calendar.WEEK_OF_YEAR, weekOfYear);
		return floorWeekOfYear(cal.getTime(), adjust);
	}

	public static Date floorWeekOfYear(Date date, Integer adjust) throws Exception {
		Calendar cal = DateUtils.toCalendar(date);
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		cal.add(Calendar.WEEK_OF_YEAR, adjust);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	public static Date ceilWeekOfYear(String year, Integer weekOfYear, Integer adjust) throws Exception {
		Date date = parse(year, format_yyyy);
		Calendar cal = DateUtils.toCalendar(date);
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		cal.set(Calendar.WEEK_OF_YEAR, weekOfYear);
		return ceilWeekOfYear(cal.getTime(), adjust);
	}

	public static Date ceilWeekOfYear(Date date, Integer adjust) throws Exception {
		Calendar cal = DateUtils.toCalendar(date);
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		cal.add(Calendar.WEEK_OF_YEAR, adjust);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		return DateUtils.ceiling(cal, Calendar.DATE).getTime();
	}

	public static Date floorMonth(String year, String month, Integer adjust) throws Exception {
		Date date = DateUtils.parseDate(StringUtils.trimToEmpty(year) + StringUtils.trimToEmpty(month), format_yyyyMM,
				formatCompact_yyyyMM);
		return floorMonth(date, adjust);
	}

	public static Date floorMonth(Date date, Integer adjust) throws Exception {
		Calendar cal = DateUtils.toCalendar(date);
		if ((null != adjust) && (adjust != 0)) {
			cal.add(Calendar.MONTH, adjust);
		}
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	public static Date ceilMonth(String year, String month, Integer adjust) throws Exception {
		Date date = DateUtils.parseDate(StringUtils.trimToEmpty(year) + StringUtils.trimToEmpty(month), format_yyyyMM,
				formatCompact_yyyyMM);
		return ceilMonth(date, adjust);
	}

	public static Date ceilMonth(Date date, Integer adjust) throws Exception {
		Calendar cal = DateUtils.toCalendar(date);
		if ((null != adjust) && (adjust != 0)) {
			cal.add(Calendar.MONTH, adjust);
		}
		return DateUtils.ceiling(cal, Calendar.MONTH).getTime();
	}

	public static Date floorDate(String year, String month, String day, Integer adjust) throws Exception {
		Date date = DateUtils.parseDate(
				StringUtils.trimToEmpty(year) + StringUtils.trimToEmpty(month) + StringUtils.trimToEmpty(day),
				format_yyyyMMdd, formatCompact_yyyyMMdd);
		return floorDate(date, adjust);
	}

	public static Date floorDate(Date date, Integer adjust) throws Exception {
		Calendar cal = DateUtils.toCalendar(date);
		if ((null != adjust) && (adjust != 0)) {
			cal.add(Calendar.DATE, adjust);
		}
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	public static Date ceilDate(String year, String month, String day, Integer adjust) throws Exception {
		Date date = DateUtils.parseDate(
				StringUtils.trimToEmpty(year) + StringUtils.trimToEmpty(month) + StringUtils.trimToEmpty(day),
				format_yyyyMMdd, formatCompact_yyyyMMdd);
		return ceilDate(date, adjust);
	}

	public static Date ceilDate(Date date, Integer adjust) throws Exception {
		Calendar cal = DateUtils.toCalendar(date);
		if ((null != adjust) && (adjust != 0)) {
			cal.add(Calendar.DATE, adjust);
		}
		return DateUtils.ceiling(cal, Calendar.DATE).getTime();
	}

	public static Date floorSeason(String year, Integer season, Integer adjust) throws Exception {
		Date date = parse(year, format_yyyy);
		Calendar calendar = DateUtils.toCalendar(date);
		switch (season) {
		case 1:
			calendar.set(Calendar.MONTH, Calendar.MARCH);
			break;
		case 2:
			calendar.set(Calendar.MONTH, Calendar.JUNE);
			break;
		case 3:
			calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
			break;
		default:
			calendar.set(Calendar.MONTH, Calendar.DECEMBER);
			break;
		}
		return floorSeason(calendar.getTime(), adjust);
	}

	public static Date floorSeason(Date date, Integer adjust) throws Exception {
		Calendar cal = DateUtils.toCalendar(date);
		switch (cal.get(Calendar.MONTH)) {
		case Calendar.JANUARY:
			cal.set(Calendar.MONTH, Calendar.JANUARY);
			break;
		case Calendar.FEBRUARY:
			cal.set(Calendar.MONTH, Calendar.JANUARY);
			break;
		case Calendar.MARCH:
			cal.set(Calendar.MONTH, Calendar.JANUARY);
			break;
		case Calendar.APRIL:
			cal.set(Calendar.MONTH, Calendar.APRIL);
			break;
		case Calendar.MAY:
			cal.set(Calendar.MONTH, Calendar.APRIL);
			break;
		case Calendar.JUNE:
			cal.set(Calendar.MONTH, Calendar.APRIL);
			break;
		case Calendar.JULY:
			cal.set(Calendar.MONTH, Calendar.JULY);
			break;
		case Calendar.AUGUST:
			cal.set(Calendar.MONTH, Calendar.JULY);
			break;
		case Calendar.SEPTEMBER:
			cal.set(Calendar.MONTH, Calendar.JULY);
			break;
		case Calendar.OCTOBER:
			cal.set(Calendar.MONTH, Calendar.OCTOBER);
			break;
		case Calendar.NOVEMBER:
			cal.set(Calendar.MONTH, Calendar.OCTOBER);
			break;
		case Calendar.DECEMBER:
			cal.set(Calendar.MONTH, Calendar.OCTOBER);
			break;
		default:
		}
		if ((null != adjust) && (adjust != 0)) {
			cal.add(Calendar.MONTH, (adjust * 3));
		}
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	public static Date ceilSeason(String year, Integer season, Integer adjust) throws Exception {
		Date date = parse(year, format_yyyy);
		Calendar calendar = DateUtils.toCalendar(date);
		switch (season) {
		case 1:
			calendar.set(Calendar.MONTH, Calendar.MARCH);
			break;
		case 2:
			calendar.set(Calendar.MONTH, Calendar.JUNE);
			break;
		case 3:
			calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
			break;
		case 4:
			calendar.set(Calendar.MONTH, Calendar.DECEMBER);
			break;
		default:
			throw new Exception("error season:" + season + ".");
		}
		return ceilSeason(calendar.getTime(), adjust);
	}

	public static Date ceilSeason(Date date, Integer adjust) throws Exception {
		Calendar cal = DateUtils.toCalendar(date);
		switch (cal.get(Calendar.MONTH)) {
		case Calendar.JANUARY:
			cal.set(Calendar.MONTH, Calendar.MARCH);
			break;
		case Calendar.FEBRUARY:
			cal.set(Calendar.MONTH, Calendar.MARCH);
			break;
		case Calendar.MARCH:
			cal.set(Calendar.MONTH, Calendar.MARCH);
			break;
		case Calendar.APRIL:
			cal.set(Calendar.MONTH, Calendar.JUNE);
			break;
		case Calendar.MAY:
			cal.set(Calendar.MONTH, Calendar.JUNE);
			break;
		case Calendar.JUNE:
			cal.set(Calendar.MONTH, Calendar.JUNE);
			break;
		case Calendar.JULY:
			cal.set(Calendar.MONTH, Calendar.SEPTEMBER);
			break;
		case Calendar.AUGUST:
			cal.set(Calendar.MONTH, Calendar.SEPTEMBER);
			break;
		case Calendar.SEPTEMBER:
			cal.set(Calendar.MONTH, Calendar.SEPTEMBER);
			break;
		case Calendar.OCTOBER:
			cal.set(Calendar.MONTH, Calendar.DECEMBER);
			break;
		case Calendar.NOVEMBER:
			cal.set(Calendar.MONTH, Calendar.DECEMBER);
			break;
		case Calendar.DECEMBER:
			cal.set(Calendar.MONTH, Calendar.DECEMBER);
			break;
		default:
		}
		if ((null != adjust) && (adjust != 0)) {
			cal.add(Calendar.MONTH, (adjust * 3));
		}
		return DateUtils.ceiling(cal, Calendar.MONTH).getTime();
	}

	public static boolean beforeNowNullIsFalse(Date date) {
		if (Objects.isNull(date)) {
			return false;
		}
		Date _now = new Date();
		return date.before(_now);
	}

	public static boolean afterNowNullIsFalse(Date date) {
		if (Objects.isNull(date)) {
			return false;
		}
		Date _now = new Date();
		return date.after(_now);
	}

	public static boolean beforeNowMinutesNullIsFalse(Date date, int minutes) {
		if (Objects.isNull(date)) {
			return false;
		}
		Date _now = new Date();
		long _interval = (_now.getTime() - date.getTime()) / 60000;
		return _interval > minutes;
	}

	public static boolean beforeNowMinutesNullIsTrue(Date date, int minutes) {
		if (Objects.isNull(date)) {
			return true;
		}
		Date _now = new Date();
		long _interval = (_now.getTime() - date.getTime()) / 60000;
		return _interval > minutes;
	}

	public static int timeOrderNumber() {
		Date date = new Date();
		long l = date.getTime() - 1514736000000L;
		return (int) (l / 1000);
	}

	public static Date cron(String expression, Date date) throws Exception {
		CronExpression exp = new CronExpression(expression);
		return exp.getNextValidTimeAfter(date);
	}

	public static Date cron(String expression) throws Exception {
		return cron(expression, new Date());
	}

	public static boolean cronAvailable(String str) {
		if (StringUtils.isEmpty(str)) {
			return false;
		}
		if (CronExpression.isValidExpression(str)) {
			return true;
		}
		return false;
	}

	public static Date fromNowMinutes(Integer minutes) {
		Date date = new Date();
		return DateUtils.addMinutes(date, minutes);
	}
	
	/**
	 * 根据需求调整年份
	 * @param startTime
	 * @param yearAdjust
	 * @param monthAdjust
	 * @param dayAdjust
	 * @return
	 */
	public static  Date getDateAfterYearAdjust( Date startTime, Integer yearAdjust, Integer monthAdjust, Integer dayAdjust) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime( startTime );
		if ((null != yearAdjust) && (yearAdjust != 0)) {
			calendar.add(Calendar.YEAR, yearAdjust );
		}
		if ((null != monthAdjust) && (monthAdjust != 0)) {
			calendar.add(Calendar.MONTH, monthAdjust );
		}
		if ((null != dayAdjust) && (dayAdjust != 0)) {
			calendar.add(Calendar.DATE, dayAdjust );
		}
		return calendar.getTime();
	}

	/**
	 * 根据需要调整时间
	 * @param dateTime
	 * @param dayAdjust
	 * @param hourAdjust
	 * @param minuteAdjust
	 * @param secondAdjust
	 * @return
	 */
	public static  Date getAdjustTimeDay( Date dateTime, Integer dayAdjust, Integer hourAdjust, Integer minuteAdjust, Integer secondAdjust) {
		Calendar calendar = Calendar.getInstance();
		if(dateTime==null){
			dateTime = new Date();
		}
		calendar.setTime( dateTime );
		if ((null != dayAdjust) && (dayAdjust != 0)) {
			calendar.add(Calendar.DAY_OF_MONTH, dayAdjust );
		}
		if ((null != hourAdjust) && (hourAdjust != 0)) {
			calendar.add(Calendar.HOUR_OF_DAY, dayAdjust );
		}
		if ((null != minuteAdjust) && (minuteAdjust != 0)) {
			calendar.add(Calendar.MINUTE, minuteAdjust );
		}
		if ((null != secondAdjust) && (secondAdjust != 0)) {
			calendar.add(Calendar.SECOND, secondAdjust );
		}
		return calendar.getTime();
	}
	
	  /** 
	  * 判断当前日期是星期几
	  * @param dateTime 修要判断的时间 
	  * @return dayForWeek 判断结果
	  * @Exception 发生异常
	  */  
	public static int dayForWeek(String dateTime ) throws Exception {
		Calendar c = Calendar.getInstance();
		c.setTime( DateUtils.parseDate( dateTime, format_yyyyMMdd) ); 
		int dayForWeek = 0 ;
		if (c.get(Calendar.DAY_OF_WEEK) == 1 ){ 
			dayForWeek = 7;
		}else {
			dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
		}
		return dayForWeek; 
	}
	
	 /** 
	  * 判断当前日期是星期几
	  * @param dateTime 修要判断的时间 
	  * @return dayForWeek 判断结果
	  * @Exception 发生异常
	  */  
	public static int dayForWeek( Date dateTime ) throws Exception {
		Calendar c = Calendar.getInstance();
		c.setTime( dateTime ); 
		int dayForWeek = 0 ;
		if (c.get(Calendar.DAY_OF_WEEK) == 1 ){ 
			dayForWeek = 7;
		}else {
			dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
		}
		return dayForWeek; 
	}

	
}