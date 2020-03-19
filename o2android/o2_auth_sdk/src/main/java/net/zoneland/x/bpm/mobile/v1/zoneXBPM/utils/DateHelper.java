package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils;

import android.text.TextUtils;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by FancyLou on 2015/10/27.
 */
public class DateHelper {
    public static SimpleDateFormat defaultFormat = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");
    public static SimpleDateFormat tf = new SimpleDateFormat("HH:mm:ss");
    public static SimpleDateFormat df = new SimpleDateFormat("MM-dd");
    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


    /**
     * 获取指定时间的日期
     *
     * @param dayCount
     * @param hour
     * @param minute
     * @param second
     * @return
     */
    public static Calendar getJustTimeDay(int dayCount, int hour, int minute,
                                          int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, dayCount);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        return calendar;
    }

    /**
     * 指定的日期，添加指定的天数
     *
     * @param date
     */
    public static void addDay(Calendar date, int dayCount) {
        date.set(Calendar.DAY_OF_YEAR, date.get(Calendar.DAY_OF_YEAR)
                + dayCount);
    }

    /**
     * 判断当前日期为双休日，双休日分为周六、周末两天。对应的数字是 7 , 1
     */
    public static boolean isSleepday(Calendar c) {
        int dw = c.get(Calendar.DAY_OF_WEEK);
        return (1 == dw || 7 == dw) ? true : false;
    }

    /**
     * 判断start 和end 两个日期是否为同一天
     */
    public static boolean eqYMD(Calendar start, Calendar end) {
        return (start.get(Calendar.YEAR) == end.get(Calendar.YEAR))
                && (start.get(Calendar.MONTH) == end.get(Calendar.MONTH))
                && (start.get(Calendar.DATE) == end.get(Calendar.DATE));
    }

    /**
     * 判断给定的start 是否小于或者等于 end
     */
    public static boolean lsOrEq(Calendar start, Calendar end) {
        return end.getTimeInMillis() - start.getTimeInMillis() >= 0 ? true
                : false;
    }


    /**
     * 求两个日期相差的天数，精确到天
     */
    public static int diffDay(Calendar start, Calendar end) {
        long total = end.getTimeInMillis() - start.getTimeInMillis();
        return (int) (total / (1000 * 60 * 60 * 24));
    }

    /**
     * 判断判断两个日期相差的天数是否有余数
     */
    public static boolean isFullDiffDay(Calendar start, Calendar end) {
        long total = end.getTimeInMillis() - start.getTimeInMillis();
        return (total) % (1000 * 60 * 60 * 24) == 0 ? true : false;
    }

    /**
     * 将 src的年月日 复制到 tar 中。
     */
    public static void cpyYMD(Calendar src, Calendar tar) {
        tar.set(Calendar.YEAR, src.get(Calendar.YEAR));
        tar.set(Calendar.MONTH, src.get(Calendar.MONTH));
        tar.set(Calendar.DAY_OF_MONTH, src.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * 将 src的时分秒 复制到 tar 中。
     */
    public static void cpyHMS(Calendar src, Calendar tar) {
        tar.set(Calendar.HOUR_OF_DAY, src.get(Calendar.HOUR_OF_DAY));
        tar.set(Calendar.MINUTE, src.get(Calendar.MINUTE));
        tar.set(Calendar.SECOND, src.get(Calendar.SECOND));
    }

    /**
     * 生成一个日期，按给定的time
     *
     * @param time 格式：HH:mm:ss
     */
    public static Calendar gc(String time) throws ParseException {
        Calendar d = Calendar.getInstance();
        d.setTime(tf.parse(time));
        return d;
    }

    public static Calendar gc(Date date) throws ParseException {
        Calendar d = Calendar.getInstance();
        d.setTime(date);
        return d;
    }

    /**
     * 给输入时间增加min分钟
     *
     * @param date
     * @param min  负数就是减多少分钟 正数就是加多少分钟
     * @return
     */
    public static Date addMinute(Date date, int min) {
        long time = date.getTime();
        long milliseconds = (time + (min * 60 * 1000));
        Calendar d = Calendar.getInstance();
        d.setTimeInMillis(milliseconds);
        return d.getTime();

    }

    /**
     * 按照给定的src复制一个日期对象，并将给定的参数设置到新的日期对象中
     *
     * @param src 源日期对象
     * @param h   时
     * @param m   分
     * @param s   秒
     */
    public static Calendar gc(Calendar src, int h, int m, int s) {
        Calendar d = (Calendar) src.clone();
        d.set(Calendar.HOUR_OF_DAY, h);
        d.set(Calendar.MINUTE, m);
        d.set(Calendar.SECOND, s);
        return d;
    }

    public static Calendar gcBirthDay(Calendar src, int year, int monthOfYear, int dayOfMonth) {
        Calendar d = (Calendar) src.clone();
        d.set(Calendar.YEAR, year);
        d.set(Calendar.MONTH, monthOfYear);
        d.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        return d;
    }

    /**
     * 按照给定的日期字符串及格式生成一个Calendar类型的日期对象
     *
     * @param date   日期字符串 如：2010-07-10 18:00:00
     * @param format 日期格式 如：yyyy-MM-dd HH:mm:ss
     */
    public static Calendar gc(String date, String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Calendar d = Calendar.getInstance();
        d.setTime(sdf.parse(date));
        return d;
    }

    /**
     * 获取当前时间，默认格式的时间字符串
     *
     * @return
     * @throws ParseException
     */
    public static String now() {
        return defaultFormat.format(new Date());
    }

    /**
     * 获取当前时间，根据传入的格式
     *
     * @param formate
     * @return
     */
    public static String nowByFormate(String formate) {
        SimpleDateFormat nowormat = new SimpleDateFormat(formate);
        return nowormat.format(new Date());
    }

    public static final Date convertStringToDate(String aMask, String strDate) {
        if (strDate == null || strDate.trim().equals("")) {
            return null;
        }
        SimpleDateFormat df = null;
        Date date = null;
        df = new SimpleDateFormat(aMask);
        try {
            date = df.parse(strDate);
        } catch (ParseException pe) {
            pe.printStackTrace();
            // throw new ParseException(pe.getMessage(), pe.getErrorOffset());
        }
        return (date);
    }

    public static final Date convertStringToDate(String strDate) {
        if (TextUtils.isEmpty(strDate)) {
            return null;
        }
        Date date = null;
        try {
            date = defaultFormat.parse(strDate);
        } catch (ParseException pe) {
            pe.printStackTrace();
            // throw new ParseException(pe.getMessage(), pe.getErrorOffset());
        }
        return (date);
    }

    public static final String getDateTime(String aMask, Date aDate) {
        SimpleDateFormat df = null;
        String returnValue = "";
        if (aDate != null) {
            df = new SimpleDateFormat(aMask);
            returnValue = df.format(aDate);
        }
        return (returnValue);
    }

    public static final String getDateTime(Date aDate) {
        String returnValue = "";
        if (aDate != null) {
            returnValue = defaultFormat.format(aDate);
        }
        return (returnValue);
    }

    public static final String getDate(Date aDate) {
        String returnValue = "";
        if (aDate != null) {
            returnValue = sdf.format(aDate);
        }
        return (returnValue);
    }


    public static String getDayBefore(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);// 日历为今天
        long tm, tm1;
        tm = cal.getTimeInMillis();// 得到当前时间与1970年1月1日0点相距的毫秒数
        tm1 = tm - (24 * 60 * 60 * 1000);// 得到昨天与1970年1月1日0点相距的毫秒数
        Date time1 = new Date(tm1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(time1);
    }

    public static Date getJustDay(int type, int count) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(type, count);
        return calendar.getTime();
    }

    /**
     * 按指定格式输出指定日期前n天的日期时间
     *
     * @param time
     * @param format
     * @return
     */
    public static String getPreStrDateByFormat(Date time, String format, int n) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -n);// 前n天的日期时间
        time = calendar.getTime();
        if (time == null) {
            return "";
        }

        if (format == null || format.equals("")) {
            return dateFormat.format(time);
        } else {
            SimpleDateFormat f = new SimpleDateFormat(format);
            return f.format(time);
        }
    }

    /**
     * 指定的日期，添加指定的天数
     *
     * @param date
     * @param dayCount
     * @return
     */
    public static Date addDay(Date date, int dayCount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, dayCount);// 指定的时间上加上n天
        date = calendar.getTime();
        return date;
    }

    /**
     * 是否小于当前时间
     *
     * @param day
     * @param format
     * @return
     */
    public static boolean isLessNow(String day, String format) {
        Date date = convertStringToDate(format, day);
        if (date == null) {
            return false;
        }
        Date today = new Date();
        if ((today.getTime() - date.getTime()) > 0) {
            return true;
        }
        return false;
    }

    /**
     * 两个时间的差距 小时数
     *
     * @param start defaultFormat
     * @param end   defaultFormat
     * @return
     */
    public static double hourGap(String start, String end) throws ParseException {
        Calendar startC = gc(start, "yyyy-MM-dd HH:mm:ss");
        Calendar endC = gc(end, "yyyy-MM-dd HH:mm:ss");
        long gapLong = endC.getTimeInMillis() - startC.getTimeInMillis();
        BigDecimal b1 = new BigDecimal(Long.toString(gapLong));
        BigDecimal b2 = new BigDecimal(Long.toString(1000 * 60 * 60));
        return b1.divide(b2, 2, BigDecimal.ROUND_HALF_EVEN).doubleValue();
    }

    public static boolean isToday(Date date) {
        String today = nowByFormate("yyyy-MM-dd");
        String dateStr = getDate(date);
        if (today.equals(dateStr)) {
            return true;
        }
        return false;
    }

    public static boolean isYesterday(Date date) {
        String dateStr = getDate(date);
        Date yesterday = addDay(new Date(), -1);
        String yesterdayStr = getDate(yesterday);
        if (dateStr.equals(yesterdayStr)) {
            return true;
        }
        return false;
    }

    /**
     * 获取星期几
     *
     * @param date
     * @param format
     * @return 周日 周一 周二 周三 周四 周五 周六
     * @throws ParseException
     */
    public static String getWeekDay(String date, String format) throws ParseException {
        Calendar c = gc(date, format);
        if (c != null) {
            int weekDay = c.get(Calendar.DAY_OF_WEEK);
            switch (weekDay) {
                case Calendar.SUNDAY:
                    return "周日";
                case Calendar.MONDAY:
                    return "周一";
                case Calendar.TUESDAY:
                    return "周二";
                case Calendar.WEDNESDAY:
                    return "周三";
                case Calendar.THURSDAY:
                    return "周四";
                case Calendar.FRIDAY:
                    return "周五";
                case Calendar.SATURDAY:
                    return "周六";
            }
        }
        return "";

    }

    /**
     * 取得指定日期所在周的第一天
     */
    public static Date getFirstDayOfWeek(Date date) {
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.SUNDAY);
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek()); // Monday
        return c.getTime();
    }

    public static Calendar getFirstDayOfWeek(Calendar cal) {
        cal.setFirstDayOfWeek(Calendar.SUNDAY);
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek()); // Monday
        return cal;
    }

    /**
     * 取得指定日期所在周的最后一天
     */
    public static Date getLastDayOfWeek(Date date) {
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.SUNDAY);
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() + 6); // Sunday
        return c.getTime();
    }

    /**
     * 当月第一天
     * @param day
     * @return
     */
    public static Calendar getMonthFirstDay(Calendar day) {
        Calendar newDay = (Calendar) day.clone();
        newDay.set(Calendar.DAY_OF_MONTH, 1);
        return newDay;
    }

    /**
     * 当月最后一天
     * @param day
     * @return
     */
    public static Calendar getMonthLastDay(Calendar day) {
        Calendar newDay = (Calendar) day.clone();
        newDay.add(Calendar.MONTH, 1);
        newDay.set(Calendar.DAY_OF_MONTH, 0);
        return newDay;
    }

    /**
     * 以友好的方式显示时间
     *
     * @param time
     * @return
     */
    public static String friendlyTime(Date time) {
        if (time == null) {
            return "Unknown";
        }
        String ftime = "";
        Calendar cal = Calendar.getInstance();

        // 判断是否是同一天
        String curDate = sdf.format(cal.getTime());
        String paramDate = sdf.format(time);
        if (curDate.equals(paramDate)) {
            int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
            if (hour == 0)
                ftime = Math.max(
                        (cal.getTimeInMillis() - time.getTime()) / 60000, 1)
                        + "分钟前";
            else
                ftime = hour + "小时前";
            return ftime;
        }

        long lt = time.getTime() / 86400000;
        long ct = cal.getTimeInMillis() / 86400000;
        int days = (int) (ct - lt);
        if (days == 0) {
            int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
            if (hour == 0)
                ftime = Math.max(
                        (cal.getTimeInMillis() - time.getTime()) / 60000, 1)
                        + "分钟前";
            else
                ftime = hour + "小时前";
        } else if (days == 1) {
            ftime = "昨天";
        } else if (days == 2) {
            ftime = "前天 ";
        } else if (days > 2 && days < 31) {
            ftime = days + "天前";
        } else if (days >= 31 && days <= 2 * 31) {
            ftime = "一个月前";
        } else if (days > 2 * 31 && days <= 3 * 31) {
            ftime = "2个月前";
        } else if (days > 3 * 31 && days <= 4 * 31) {
            ftime = "3个月前";
        } else {
            ftime = sdf.format(time);
        }
        return ftime;
    }

    /**
     * 聊天界面 显示的消息时间
     * @param time
     * @return
     */
    public static String imChatMessageTime(String time) {
        if (time == null || time.isEmpty()) {
            return "";
        }
        Date date = convertStringToDate(time);
        if (date == null) {
            return "";
        }
        Calendar cal = Calendar.getInstance();
        long lt = date.getTime() / 86400000;
        long ct = cal.getTimeInMillis() / 86400000;
        int days = (int) (ct - lt);
        String ftime = "";
        if (days == 0) {
            ftime = getDateTime("HH:mm", date);
        }else if (days == 1) {
            ftime = "昨天 " + getDateTime("HH:mm", date);
        } else if (days == 2) {
            ftime = "前天 " + getDateTime("HH:mm", date);
        }else {
            ftime = getDateTime("MM-dd HH:mm", date);
        }
        return ftime;
    }

    /**
     * end是否大于start 1分钟以上
     * @param start
     * @param end
     * @return
     */
    public static boolean imChatTimeBiggerThan1Minute(String start, String end) {
        Date date1 = convertStringToDate(start);
        Date date2 = convertStringToDate(end);
        if (date1 !=null && date2 != null) {
            long second1 = date1.getTime() / 1000 ;
            long second2 = date2.getTime() / 1000 ;
            if ((second2 - second1) > 60 ) {
                return true;
            }
        }
        return false;
    }

    /**
     * 切割两个时间间隔的天数 包含开始和结束
     * @param startTime
     * @param endTime
     * @return
     */
    @NotNull
    public static List<Calendar> splitEveryDay(@NotNull String startTime, @NotNull String endTime) {
        List<Calendar> list = new ArrayList<>();
        try {
            Calendar start = gc(startTime, "yyyy-MM-dd HH:mm:ss");
            Calendar end = gc(endTime, "yyyy-MM-dd HH:mm:ss");
            if (isSameDay(start, end)) {
                list.add(start);
            }else {
                if (start.get(Calendar.YEAR) == end.get(Calendar.YEAR)) {
                    list.add(start);
                    int gap = end.get(Calendar.DAY_OF_YEAR) - start.get(Calendar.DAY_OF_YEAR);
                    for (int i = 0; i < gap; i++) {
                        Calendar nwDay = (Calendar) start.clone();
                        nwDay.set(Calendar.DAY_OF_YEAR, nwDay.get(Calendar.DAY_OF_YEAR)+ i+1);
                        list.add(nwDay);
                    }
                }else {
                    list.add(start);
                    int max = start.getActualMaximum(Calendar.DAY_OF_YEAR);
                    int startDay = start.get(Calendar.DAY_OF_YEAR);
                    int gap = max - startDay + end.get(Calendar.DAY_OF_YEAR);
                    for (int i = 0; i < gap; i++) {
                        Calendar nwDay = (Calendar) start.clone();
                        nwDay.set(Calendar.DAY_OF_YEAR, nwDay.get(Calendar.DAY_OF_YEAR)+ i+1);
                        list.add(nwDay);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean isSameDay(Calendar start, Calendar end) {
        return (start.get(Calendar.YEAR) == end.get(Calendar.YEAR) && start.get(Calendar.DAY_OF_YEAR) == end.get(Calendar.DAY_OF_YEAR));
    }
}
