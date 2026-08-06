package com.x.attendance.assemble.control.jaxrs.v2;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.attendance.entity.v2.AttendanceV2ShiftCheckTime;
import com.x.attendance.entity.v2.AttendanceV2ShiftCheckTimeProperties;
import com.x.base.core.project.tools.DateTools;

/**
 * 班次工作时间计算，休息时间段不计入工作时长。
 */
public class AttendanceV2ShiftWorkTimeHelper {

    private static final long MINUTE_MILLISECONDS = 60 * 1000;

    private AttendanceV2ShiftWorkTimeHelper() {
    }

    public static long shiftWorkMinutes(AttendanceV2ShiftCheckTimeProperties properties) throws Exception {
        long minutes = 0;
        for (AttendanceV2ShiftCheckTime checkTime : properties.getTimeList()) {
            minutes += standardWorkMinutes("2000-01-01", checkTime);
        }
        return minutes;
    }

    public static long standardWorkMinutes(String date, AttendanceV2ShiftCheckTime checkTime) throws Exception {
        long minutes = 0;
        for (TimeRange range : listWorkRange(date, checkTime)) {
            minutes += rangeMinutes(range.getStart(), range.getEnd());
        }
        return minutes;
    }

    public static long overlapWorkMinutes(Date startTime, Date endTime, String date,
            AttendanceV2ShiftCheckTime checkTime) throws Exception {
        long minutes = 0;
        for (TimeRange range : listWorkRange(date, checkTime)) {
            minutes += overlapMinutes(startTime, endTime, range.getStart(), range.getEnd());
        }
        return minutes;
    }

    public static List<TimeRange> listRestRange(String date, AttendanceV2ShiftCheckTime checkTime) throws Exception {
        List<TimeRange> rangeList = new ArrayList<>();
        if (checkTime == null || StringUtils.isBlank(checkTime.getOnDutyTime())
                || StringUtils.isBlank(checkTime.getOffDutyTime())) {
            return rangeList;
        }
        Date onDuty = parseDateTime(date, checkTime.getOnDutyTime());
        Date offDuty = parseDateTime(date, checkTime.getOffDutyTime());
        if (BooleanUtils.isTrue(checkTime.getOffDutyNextDay())) {
            offDuty = DateTools.addDay(offDuty, 1);
        }
        if (!offDuty.after(onDuty)) {
            return rangeList;
        }
        TimeRange dutyRange = new TimeRange(onDuty, offDuty);
        TimeRange restRange = parseRestRange(date, checkTime, dutyRange);
        if (restRange == null) {
            return rangeList;
        }
        Date restStart = maxDate(restRange.getStart(), dutyRange.getStart());
        Date restEnd = minDate(restRange.getEnd(), dutyRange.getEnd());
        if (restEnd.after(restStart)) {
            rangeList.add(new TimeRange(restStart, restEnd));
        }
        return rangeList;
    }

    public static List<TimeRange> listWorkRange(String date, AttendanceV2ShiftCheckTime checkTime) throws Exception {
        List<TimeRange> rangeList = new ArrayList<>();
        if (checkTime == null || StringUtils.isBlank(checkTime.getOnDutyTime())
                || StringUtils.isBlank(checkTime.getOffDutyTime())) {
            return rangeList;
        }
        Date onDuty = parseDateTime(date, checkTime.getOnDutyTime());
        Date offDuty = parseDateTime(date, checkTime.getOffDutyTime());
        if (BooleanUtils.isTrue(checkTime.getOffDutyNextDay())) {
            offDuty = DateTools.addDay(offDuty, 1);
        }
        if (!offDuty.after(onDuty)) {
            return rangeList;
        }
        TimeRange dutyRange = new TimeRange(onDuty, offDuty);
        TimeRange restRange = parseRestRange(date, checkTime, dutyRange);
        if (restRange == null) {
            rangeList.add(dutyRange);
            return rangeList;
        }
        Date restStart = maxDate(restRange.getStart(), dutyRange.getStart());
        Date restEnd = minDate(restRange.getEnd(), dutyRange.getEnd());
        if (!restEnd.after(restStart)) {
            rangeList.add(dutyRange);
            return rangeList;
        }
        if (restStart.after(dutyRange.getStart())) {
            rangeList.add(new TimeRange(dutyRange.getStart(), restStart));
        }
        if (dutyRange.getEnd().after(restEnd)) {
            rangeList.add(new TimeRange(restEnd, dutyRange.getEnd()));
        }
        return rangeList;
    }

    public static boolean restPeriodAvailable(String restPeriod) {
        if (StringUtils.isBlank(restPeriod)) {
            return true;
        }
        String[] times = restPeriod.split("-");
        return times.length == 2 && hourMinuteAvailable(times[0]) && hourMinuteAvailable(times[1]);
    }

    private static TimeRange parseRestRange(String date, AttendanceV2ShiftCheckTime checkTime, TimeRange dutyRange)
            throws Exception {
        if (StringUtils.isBlank(checkTime.getRestPeriod())) {
            return null;
        }
        String[] times = checkTime.getRestPeriod().split("-");
        if (times.length != 2) {
            return null;
        }
        Date restStart = parseDateTime(date, StringUtils.trim(times[0]));
        Date restEnd = parseDateTime(date, StringUtils.trim(times[1]));
        if (!restEnd.after(restStart)) {
            restEnd = DateTools.addDay(restEnd, 1);
        }
        if (BooleanUtils.isTrue(checkTime.getOffDutyNextDay()) && restStart.before(dutyRange.getStart())) {
            restStart = DateTools.addDay(restStart, 1);
            restEnd = DateTools.addDay(restEnd, 1);
        }
        return new TimeRange(restStart, restEnd);
    }

    private static Date parseDateTime(String date, String time) throws Exception {
        return DateTools.parse(date + " " + StringUtils.trim(time), DateTools.format_yyyyMMddHHmm);
    }

    private static boolean hourMinuteAvailable(String time) {
        String[] parts = StringUtils.trimToEmpty(time).split(":");
        if (parts.length != 2) {
            return false;
        }
        try {
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);
            return hour >= 0 && hour <= 23 && minute >= 0 && minute <= 59;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static long rangeMinutes(Date startTime, Date endTime) {
        if (startTime == null || endTime == null || !endTime.after(startTime)) {
            return 0;
        }
        return (endTime.getTime() - startTime.getTime()) / MINUTE_MILLISECONDS;
    }

    private static long overlapMinutes(Date startTime, Date endTime, Date rangeStart, Date rangeEnd) {
        long start = Math.max(startTime.getTime(), rangeStart.getTime());
        long end = Math.min(endTime.getTime(), rangeEnd.getTime());
        if (end <= start) {
            return 0;
        }
        return (end - start) / MINUTE_MILLISECONDS;
    }

    private static Date maxDate(Date d1, Date d2) {
        return d1.after(d2) ? d1 : d2;
    }

    private static Date minDate(Date d1, Date d2) {
        return d1.before(d2) ? d1 : d2;
    }

    public static class TimeRange {

        private Date start;

        private Date end;

        public TimeRange(Date start, Date end) {
            this.start = start;
            this.end = end;
        }

        public Date getStart() {
            return start;
        }

        public Date getEnd() {
            return end;
        }
    }
}
