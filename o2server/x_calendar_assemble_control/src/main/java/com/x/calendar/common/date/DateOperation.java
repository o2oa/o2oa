package com.x.calendar.common.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

/**
 * 
 * @包 名 ：com.land.util.date
 * @文件名 ：DateUtil.java
 * @作 者 ：O2LEE
 * @单 位 ：浙江兰德纵横网络技术有限公司
 * @日 期 ：2013-06-07
 */
public class DateOperation {

	private Logger logger = LoggerFactory.getLogger(DateOperation.class);

	/**
	 * 根据样式得到格式化对象SimpleDateFormat
	 * 
	 * @param date
	 * @param style
	 * @return
	 */
	public String getDate(Date date, String style) {
		SimpleDateFormat format = new SimpleDateFormat(style);
		return format.format(date);
	}

	/**
	 * 得到某日期的日期部分
	 * 
	 * @param date
	 * @return yyyy-MM-dd
	 */
	public String getDate(Date date) {
		return format1.format(date);
	}

	/**
	 * 得到某日期的时间部分
	 * 
	 * @param date
	 * @return HH:mm:ss
	 */
	public String getTime(Date date) {
		return format2.format(date);
	}

	public Date getDateFromString(String dateString, String style) throws Exception {
		Date date = null;
		if (style == null || "".equals(style.trim())) {
			date = format1.parse(dateString);
		} else if ("yyyy-MM-dd HH:mm:ss".equals(style.trim())) {
			date = format3.parse(dateString);
		} else if ("yyyy/MM/dd HH:mm:ss".equals(style.trim())) {
			date = format3_1.parse(dateString);
		} else if ("yyyy/MM/dd".equals(style.trim())) {
			date = format4.parse(dateString);
		} else if ("yyyy-MM-dd".equals(style.trim())) {
			date = format1.parse(dateString);
		} else if ("yyyyMMdd".equals(style.trim())) {
			date = format5.parse(dateString);
		} else if ("yyyyMMddHHmmss".equals(style.trim())) {
			date = format7.parse(dateString);
		} else if ("HH:mm:ss".equals(style.trim())) {
			date = format2.parse(dateString);
		} else {
			throw new Exception("对不起，您输入的日期style系统无法识别，请检查您的参数输入！");
		}
		return date;
	}

	/**
	 * 将字符串转换为日期格式 会尝试多种格式转换，转换成功后返回结果
	 * 
	 * @param dateString
	 * @return
	 * @throws Exception
	 */
	public Date getDateFromString(String dateString) throws Exception {
		Date date = null;
		try {
			date = getDateFromString(dateString, "yyyy-MM-dd HH:mm:ss");
			return date;
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			date = getDateFromString(dateString, "yyyy/MM/dd HH:mm:ss");
			return date;
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			date = getDateFromString(dateString, "yyyy-MM-dd");
			return date;
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			date = getDateFromString(dateString, "yyyy/MM/dd");
			return date;
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			date = getDateFromString(dateString, "yyyyMMdd");
			return date;
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			date = getDateFromString(dateString, "yyyyMMddHHmmss");
			return date;
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			date = getDateFromString(dateString, "HH:mm:ss");
			return date;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}

	public String getDateFromDate(Date date, String style) throws Exception {
		String dateString = null;
		if (style == null || "".equals(style.trim())) {
			dateString = format1.format(date);
		} else if ("yyyy-MM-dd HH:mm:ss".equals(style.trim())) {
			dateString = format3.format(date);
		} else if ("yyyy/MM/dd HH:mm:ss".equals(style.trim())) {
			dateString = format3_1.format(date);
		} else if ("yyyy/MM/dd".equals(style.trim())) {
			dateString = format4.format(date);
		} else if ("yyyy-MM-dd".equals(style.trim())) {
			dateString = format1.format(date);
		} else if ("yyyyMMdd".equals(style.trim())) {
			dateString = format5.format(date);
		} else if ("yyyyMMddHHmmss".equals(style.trim())) {
			dateString = format7.format(date);
		} else if ("HH:mm:ss".equals(style.trim())) {
			dateString = format2.format(date);
		} else {
			throw new Exception("对不起，您输入的日期style系统无法识别，请检查您的参数输入！style=" + style);
		}
		return dateString;
	}

	/**
	 * 得到某日期加上或减去天数后的日期,day为负数时减去
	 * 
	 * @param date
	 * @param month
	 * @return "yyyy-MM-dd"
	 * @throws Exception
	 */
	public String getDayAdd(String dateString, int day, String style) throws Exception {
		Date date = getDateFromString(dateString, style);
		return getDayAdd(date, day);
	}

	/**
	 * 得到某日期加上或减去天数后的日期,day为负数时减去
	 * 
	 * @param date
	 * @param month
	 * @return "yyyy-MM-dd"
	 */
	public String getDayAdd(Date date, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, day);
		return format1.format(calendar.getTime());
	}

	/**
	 * 得到某日期加上或减去天数后的日期,day为负数时减去
	 * 
	 * @param date
	 * @param month
	 * @return "yyyy-MM-dd"
	 */
	public Date getDayAddDate(Date date, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, day);
		return calendar.getTime();
	}

	/**
	 * 得到某日期加上或减去月份后的日期,month为负数时减去
	 * 
	 * @param date
	 * @param month
	 * @return "yyyy-MM-dd"
	 */
	public String getMonthAdd(Date date, int month) {
		return format1.format(getMonthAddDate(date, month));
	}

	/**
	 * 得到某日期加上或减去月份后的日期,month为负数时减去
	 * 
	 * @param date
	 * @param month
	 * @return "yyyy-MM-dd"
	 */
	public Date getMonthAddDate(Date date, int month) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, month);
		return calendar.getTime();
	}

	/**
	 * 得到某日期加上或减去分钟后的日期,min为负数时减去
	 * 
	 * @param date
	 * @param month
	 * @return "yyyy-MM-dd HH:mi:ss"
	 */
	public String getMinutesAdd(Date date, int min) {
		return format3.format(getMinutesAddDate(date, min));
	}

	/**
	 * 得到某日期加上或减去分钟后的日期,min为负数时减去
	 * 
	 * @param date
	 * @param month
	 * @return "yyyy-MM-dd HH:mi:ss"
	 */
	public Date getMinutesAddDate(Date date, int min) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MINUTE, min);
		return calendar.getTime();
	}

	/**
	 * 得到某日期的日
	 * 
	 * @param Date date
	 * @return
	 */
	public String getDay(Date date) {
		try {
			return format10.format(date);
		} catch (Exception e) {
			return "0";
		}
	}

	public int getDayNumber(Date date) {
		String result = null;
		try {
			result = format10.format(date);
		} catch (Exception e) {
			result = "0";
		}
		return Integer.parseInt(result);
	}

	/**
	 * 得到某日期的月份
	 * 
	 * @param Date date
	 * @return
	 */
	public String getMonth(Date date) {
		try {
			return format9.format(date);
		} catch (Exception e) {
			return "0";
		}
	}

	/**
	 * 得到某日期的年份-月份：yyyy-MM
	 * 
	 * @param Date date
	 * @return
	 */
	public String getYearAndMonth(Date date) {
		try {
			return format_year_month.format(date);
		} catch (Exception e) {
			System.out.println("date is not valid, date:" + date);
			return null;
		}
	}

	public int getMonthNumber(Date date) {
		String result = null;
		try {
			result = format9.format(date);
		} catch (Exception e) {
			result = "0";
		}
		return Integer.parseInt(result);
	}

	/**
	 * 得到某日期的年份
	 * 
	 * @param Date date
	 * @return
	 */
	public String getYear(Date date) {
		try {
			return format8.format(date);
		} catch (Exception e) {
			return "0";
		}
	}

	public int getYearNumber(Date date) {
		String result = null;
		try {
			result = format8.format(date);
		} catch (Exception e) {
			result = "0";
		}
		return Integer.parseInt(result);
	}

	/**
	 * 得到某日期的小时
	 * 
	 * @param Date date
	 * @return
	 */
	public String getHour(Date date) {
		try {
			return format11.format(date);
		} catch (Exception e) {
			return "0";
		}
	}

	/**
	 * 得到某日期的分钟
	 * 
	 * @param Date date
	 * @return
	 */
	public String getMinites(Date date) {
		return format12.format(date);
	}

	/**
	 * 得到某日期的秒
	 * 
	 * @param Date date
	 * @return
	 */
	public String getSeconds(Date date) {
		return format13.format(date);
	}

	/**
	 * 得到某年有多少天
	 * 
	 * @param String date "yyyy-MM-dd"
	 * @return
	 * @throws ParseException
	 */
	@SuppressWarnings("static-access")
	public int getDaysForYear(String date) throws ParseException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(format1.parse(date));
		return calendar.get(calendar.DAY_OF_YEAR);
	}

	/**
	 * 得到某年有多少天
	 * 
	 * @param Date date
	 * @return
	 * @throws ParseException
	 */
	@SuppressWarnings("static-access")
	public int getDaysForYear(Date date) throws ParseException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(calendar.DAY_OF_YEAR);
	}

	/**
	 * 得到某年有多少天
	 * 
	 * @param String year "yyyy"
	 * @return
	 * @throws ParseException
	 */
	@SuppressWarnings("static-access")
	public int getDaysForYear_YYYY(String year) throws ParseException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(format8.parse(year));
		return calendar.get(calendar.DAY_OF_YEAR);
	}

	/**
	 * 得到某月有多少天
	 * 
	 * @param String date "yyyy-MM-dd"
	 * @return
	 * @throws ParseException
	 */
	@SuppressWarnings("static-access")
	public int getDaysForMonth(String date) throws ParseException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(format1.parse(date));
		return calendar.get(calendar.DAY_OF_MONTH);
	}

	/**
	 * 得到某月有多少天
	 * 
	 * @param String date "yyyy-MM"
	 * @return
	 * @throws ParseException
	 */
	@SuppressWarnings("static-access")
	public int getDaysForMonth_MM(String date) throws ParseException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(format14.parse(date));
		return calendar.get(calendar.DAY_OF_MONTH);
	}

	/**
	 * 得到某月有多少天
	 * 
	 * @param Date date
	 * @return
	 * @throws ParseException
	 */
	public int getDaysForMonth(Date date) throws ParseException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	public int getMaxDayByYearMonth(int year, int month) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		return calendar.getActualMaximum(Calendar.DATE);
	}

	/**
	 * 得到当前的日期
	 * 
	 * @return
	 */
	public String getNowDate() {
		return format1.format(new Date());
	}

	/**
	 * 得到当前的时间
	 * 
	 * @return
	 */
	public String getNowTime() {
		return format2.format(new Date());
	}

	/**
	 * 得到当前的时间 yyyyMMddHHmmss
	 * 
	 * @return
	 */
	public String getNowTimeChar() {
		return format7.format(new Date());
	}

	/**
	 * 得到当前的时间 yyyy-MM-dd HH:mm:ss
	 * 
	 * @return
	 */
	public String getNowDateTime() {
		return format3.format(new Date());
	}

	/**
	 * 得到两个时间之前的分差
	 * 
	 * @param date1 yyyy-MM-dd HH:mm:ss
	 * @param date2 yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public long getDeff(String date1, String date2) {
		long dayNumber = 0;
		// 1小时=60分钟=3600秒=3600000
		long mins = 60L * 1000L;
		// long day= 24L * 60L * 60L * 1000L;计算天数之差
		SimpleDateFormat df = null;
		if (date1.length() == 19) {
			df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		} else if (date1.length() == 10) {
			df = new SimpleDateFormat("yyyy-MM-dd");
		} else if (date1.length() == 8) {
			df = new SimpleDateFormat("HH:mm:ss");
		} else {
			df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
		try {
			java.util.Date d1 = df.parse(date1);
			java.util.Date d2 = df.parse(date2);
			dayNumber = (d2.getTime() - d1.getTime()) / mins;
		} catch (Exception e) {
			logger.warn("系统获取得到两个时间之前的分差发生异常");
			logger.error(e);
		}
		return dayNumber;
	}

	/**
	 * 得到两个时间之前的分差
	 * 
	 * @param date1 yyyy-MM-dd HH:mm:ss
	 * @param date2 yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public long getDeff(Date date1, Date date2) {
		long dayNumber = 0;
		// 1小时=60分钟=3600秒=3600000
		long mins = 60L * 1000L;
		// long day= 24L * 60L * 60L * 1000L;计算天数之差
		try {
			dayNumber = (date2.getTime() - date1.getTime()) / mins;
		} catch (Exception e) {
			logger.warn("系统获取得到两个时间之前的分差发生异常");
			logger.error(e);
		}
		return dayNumber;
	}

	/**
	 * 
	 * 日期格式转换 从YYYY-MM-DD转换到YYYYMMDD
	 * 
	 * @param date
	 * @throws ParseException
	 */
	public String changeDateFormat(String dateString) {
		Date date;
		String reslut = null;
		try {
			if ("".equals(dateString)) {
				dateString = "0000-00-00";
			}
			date = format1.parse(dateString);
			reslut = format5.format(date);
		} catch (ParseException e) {
			try {
				date = format1.parse("0000-00-00");
			} catch (ParseException e1) {
				logger.warn("系统日期格式转换发生异常");
				logger.error(e);
			}
			logger.warn("系统日期格式转换发生异常");
			logger.error(e);
		}

		return reslut;
	}

	/** 格式 yyyy-MM-dd */
	public final SimpleDateFormat format_year_month = new SimpleDateFormat("yyyy-MM");
	/** 格式 yyyy-MM-dd */
	public final SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
	/** 格式 HH:mm:ss */
	public final SimpleDateFormat format2 = new SimpleDateFormat("HH:mm:ss");
	/** 格式 yyyy-MM-dd HH:mm:ss */
	public final SimpleDateFormat format3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public final SimpleDateFormat format3_1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	/** 格式 yyyy/MM/dd */
	public final SimpleDateFormat format4 = new SimpleDateFormat("yyyy/MM/dd");
	/** 格式 yyyyMMdd */
	public final SimpleDateFormat format5 = new SimpleDateFormat("yyyyMMdd");
	/** 格式 HHmmss */
	public final SimpleDateFormat format6 = new SimpleDateFormat("HHmmss");
	/** 格式 yyyyMMddHHmmss */
	public final SimpleDateFormat format7 = new SimpleDateFormat("yyyyMMddHHmmss");
	/** 格式 yyyy */
	public final SimpleDateFormat format8 = new SimpleDateFormat("yyyy");
	/** 格式 MM */
	public final SimpleDateFormat format9 = new SimpleDateFormat("MM");
	/** 格式 dd */
	public final SimpleDateFormat format10 = new SimpleDateFormat("dd");
	/** 格式 HH */
	public final SimpleDateFormat format11 = new SimpleDateFormat("HH");
	/** 格式 mm */
	public final SimpleDateFormat format12 = new SimpleDateFormat("mm");
	/** 格式 ss */
	public final SimpleDateFormat format13 = new SimpleDateFormat("ss");
	/** 格式 ss */
	public final SimpleDateFormat format14 = new SimpleDateFormat("yyyy-MM");

	public String getStartOfWeek(String dateString) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date = format.parse(dateString);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.setFirstDayOfWeek(Calendar.MONDAY);
			int tmp = cal.get(Calendar.DAY_OF_WEEK) - 1;
			if (0 == tmp) {
				tmp = 7;
			}
			cal.add(Calendar.DATE, -(tmp - 1));
			return getDateFromDate(cal.getTime(), "yyyy-MM-dd") + " 00:00:00";
		} catch (ParseException e) {
			logger.warn("系统getStartOfWeek日期格式转换发生异常");
			logger.error(e);
		} catch (Exception e) {
			logger.warn("系统getStartOfWeek发生异常");
			logger.error(e);
		}
		return null;
	}

	public String getEndOfWeek(String dateString) {
		try {
			Date date = getDateFromString(getStartOfWeek(dateString), "yyyy-MM-dd HH:mm:ss");
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.setFirstDayOfWeek(Calendar.MONDAY);
			cal.add(Calendar.DATE, 6);
			return getDateFromDate(cal.getTime(), "yyyy-MM-dd") + " 23:59:59";
		} catch (ParseException e) {
			logger.warn("系统getEndOfWeek日期格式转换发生异常");
			logger.error(e);
		} catch (Exception e) {
			logger.warn("系统getEndOfWeek发生异常");
			logger.error(e);
		}
		return null;
	}

	/**
	 * 将时间格式转换为 **月**日**时**分的格式
	 * 
	 * @param dateString
	 * @param style
	 * @return
	 * @throws Exception
	 */
	public String getDateCNString(String dateString, String style) throws Exception {
		StringBuffer ch_date_string = new StringBuffer();
		Date _date = null;
		try {
			_date = getDateFromString(dateString, style);
		} catch (Exception e) {
			logger.warn("系统getDateCNString日期格式转换发生异常");
			logger.error(e);
		}
		if (_date == null) {
			_date = new Date();
		}
		String year = getYear(_date);
		String month = getMonth(_date);
		String day = getDay(_date);
		String hour = getHour(_date);
		String min = getMinites(_date);
		ch_date_string.append(year);
		ch_date_string.append("年");
		ch_date_string.append(month);
		ch_date_string.append("月");
		ch_date_string.append(day);
		ch_date_string.append("日");
		ch_date_string.append(hour);
		ch_date_string.append("时");
		ch_date_string.append(min);
		ch_date_string.append("分");
		return ch_date_string.toString();
	}

	/**
	 * 将时间格式转换为 **月**日**时**分**秒 的格式
	 * 
	 * @param dateString
	 * @param style
	 * @return
	 * @throws Exception
	 */
	public String getDateCNString2(String dateString, String style) throws Exception {
		StringBuffer ch_date_string = new StringBuffer();
		Date _date = null;
		try {
			_date = getDateFromString(dateString, style);
		} catch (Exception e) {
			logger.warn("系统getDateCNString2日期格式转换发生异常");
			logger.error(e);
		}
		if (_date == null) {
			_date = new Date();
		}
		String year = getYear(_date);
		String month = getMonth(_date);
		String day = getDay(_date);
		String hour = getHour(_date);
		String min = getMinites(_date);
		String sec = getSeconds(_date);
		ch_date_string.append(year);
		ch_date_string.append("年");
		ch_date_string.append(month);
		ch_date_string.append("月");
		ch_date_string.append(day);
		ch_date_string.append("日");
		ch_date_string.append(hour);
		ch_date_string.append("时");
		ch_date_string.append(min);
		ch_date_string.append("分");
		ch_date_string.append(sec);
		ch_date_string.append("秒");
		return ch_date_string.toString();
	}

	/**
	 * 获取日期在一年中的周数 结果从1开始
	 * 
	 * @param dateString yyyy-mm-dd
	 * @return
	 */
	public int getWeekNumOfYear(String dateString) {
		try {
			Date date = getDateFromString(dateString, "yyyy-MM-dd");
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.setFirstDayOfWeek(2);// 设置每周的第一天是星期一
			// 月份有问题(这里的月份开始计数为0)
			// 本年的第几天,在计算时间间隔的时候有用
			// System.out.println("一年中的天数:" + cal.get(Calendar.DAY_OF_YEAR));
			// System.out.println("一年中的周数:" + cal.get(Calendar.WEEK_OF_YEAR));
			// 即本月的第几周
			// System.out.println("一月中的周数:" + cal.get(Calendar.WEEK_OF_MONTH));
			// 即一周中的第几天(这里是以周日为第一天的)
			// System.out.println("一周中的天数:" + cal.get(Calendar.DAY_OF_WEEK));
			return cal.get(Calendar.WEEK_OF_YEAR);
		} catch (ParseException e) {
			logger.warn("系统getWeekNumOfYear日期格式转换发生异常");
			logger.error(e);
		} catch (Exception e) {
			logger.warn("系统getWeekNumOfYear发生异常");
			logger.error(e);
		}
		return -1;
	}

	public Date getFirstDayInMonth(Date date) {
		int year = getYearNumber(date);
		int month = getMonthNumber(date);
		Calendar cal = Calendar.getInstance();
		cal.set(year, month - 1, 1);
		return cal.getTime();
	}

	public Date getEndDayInMonth(Date date) {
		int year = getYearNumber(date);
		int month = getMonthNumber(date);
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, 1);
		cal.add(Calendar.DATE, -1);
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), 23, 59, 59);
		return cal.getTime();
	}

	/**
	 * 计算两个日期或者时间之间的月份列表：yyyy-MM
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public List<String> listMonthsBetweenDate(Date minTime, Date maxTime) {
		if (maxTime == null) {
			maxTime = new Date();
		}
		ArrayList<String> result = new ArrayList<String>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");// 格式化为年月
		Calendar min = Calendar.getInstance();
		Calendar max = Calendar.getInstance();

		min.setTime(minTime);
		min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);

		max.setTime(maxTime);
		max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);

		result = addMonthToStrList(sdf.format(minTime), result);
		result = addMonthToStrList(sdf.format(maxTime), result);
		Calendar curr = min;
		while (curr.before(max)) {
			result = addMonthToStrList(sdf.format(curr.getTime()), result);
			curr.add(Calendar.MONTH, 1);
		}
		return result;
	}

	private ArrayList<String> addMonthToStrList(String monthString, ArrayList<String> result) {
		if (result == null) {
			result = new ArrayList<>();
		}
		if (!result.contains(monthString)) {
			result.add(monthString);
		}
		return result;
	}

	/**
	 * 根据时间获取当天的开始时间 yyyy-MM-dd 00:00:00
	 * 
	 * @param startTime
	 * @return
	 * @throws Exception
	 */
	public Date getBeginTimeInDay(Date startTime) throws Exception {
		if (startTime == null) {
			startTime = new Date();
		}
		return getDateFromString(format1.format(startTime) + " 00:00:00");
	}

	/**
	 * 根据时间获取当天的开始时间 yyyy-MM-dd 00:00:00
	 * 
	 * @param startTime
	 * @return
	 * @throws Exception
	 */
	public Date getEndTimeInDay(Date endTime) throws Exception {
		if (endTime == null) {
			endTime = new Date();
		}
		return getDateFromString(format1.format(endTime) + " 23:59:59");
	}

	/**
	 * 计算两个时间之间涉及的日期列表
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public List<String> listDaysBetweenDate(Date startTime, Date endTime) {
		if (endTime == null) {
			endTime = new Date();
		}
		ArrayList<String> result = new ArrayList<String>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");// 格式化为年月日
		Calendar min = Calendar.getInstance();
		Calendar max = Calendar.getInstance();

		min.setTime(startTime);
		min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), min.get(Calendar.DATE));

		max.setTime(endTime);
		max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), max.get(Calendar.DATE));

		result = addMonthToStrList(sdf.format(startTime), result);
		result = addMonthToStrList(sdf.format(endTime), result);
		Calendar curr = min;
		while (curr.before(max)) {
			result = addMonthToStrList(sdf.format(curr.getTime()), result);
			curr.add(Calendar.DATE, 1);
		}
		return result;
	}

	/**
	 * 根据开始时间，和需要变动的时间来生成一个新的时间
	 * 
	 * @param startTime
	 * @param day
	 * @param hour
	 * @param min
	 * @param sec
	 * @return
	 */
	public Date caculateNewDate(Date startTime, int day, int hour, int min, int sec) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(startTime);
		cal.add(Calendar.DATE, day);
		cal.add(Calendar.HOUR, hour);
		cal.add(Calendar.MINUTE, min);
		cal.add(Calendar.SECOND, sec);
		return cal.getTime();
	}

	public static void main(String[] args) throws Exception {
		DateOperation dateOperation = new DateOperation();
		Date time = dateOperation.caculateNewDate(dateOperation.getDateFromString("2018-05-19 08:00:00"), 0, 0, -15, 0);
		System.out.println(time);
	}
}
