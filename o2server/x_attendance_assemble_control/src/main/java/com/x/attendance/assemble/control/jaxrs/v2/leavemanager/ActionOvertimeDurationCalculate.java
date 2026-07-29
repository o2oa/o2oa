package com.x.attendance.assemble.control.jaxrs.v2.leavemanager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.AttendanceV2RestDayHelper;
import com.x.attendance.assemble.control.jaxrs.v2.AttendanceV2ShiftWorkTimeHelper;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionNotExistObject;
import com.x.attendance.assemble.control.jaxrs.v2.WoGroupShift;
import com.x.attendance.assemble.control.jaxrs.v2.detail.ExceptionDateEndBeforeStartError;
import com.x.attendance.entity.v2.AttendanceV2Shift;
import com.x.attendance.entity.v2.AttendanceV2ShiftCheckTime;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.tools.DateTools;

public class ActionOvertimeDurationCalculate extends BaseAction {

    ActionResult<Wo> execute(JsonElement jsonElement) throws Exception {
        ActionResult<Wo> result = new ActionResult<>();
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            if (StringUtils.isEmpty(wi.getPerson())) {
                throw new ExceptionEmptyParameter("人员标识");
            }
            Date startTime = wi.getStartTime();
            Date endTime = wi.getEndTime();
            if (startTime == null) {
                throw new ExceptionEmptyParameter("开始时间");
            }
            if (endTime == null) {
                throw new ExceptionEmptyParameter("结束时间");
            }
            if (startTime.after(endTime)) {
                throw new ExceptionDateEndBeforeStartError();
            }

            Business business = new Business(emc);
            Person person = business.organization().person().getObject(wi.getPerson(), true);
            if (person == null) {
                throw new ExceptionNotExistObject("人员 " + wi.getPerson());
            }

            Date startDate = DateTools.floorDate(startTime, 0);
            Date endDate = DateTools.floorDate(startTime.before(endTime) ? DateTools.addMinutes(endTime, -1) : endTime,
                    0);
            String personDn = person.getDistinguishedName();
            List<String> dateList = AttendanceV2RestDayHelper.listDateRange(startDate, endDate);
            List<String> restDateList = AttendanceV2RestDayHelper.listRestDate(business, personDn, dateList);
            List<TimeRange> normalWorkRangeList = listNormalWorkRange(business, personDn, DateTools.addDay(startDate, -1),
                    endDate);
            long durationMinutes = calculateOvertimeMinutes(startTime, endTime, normalWorkRangeList);
            Map<String, Long> standardWorkMinutesMap = mapStandardWorkMinutes(business, personDn, dateList,
                    restDateList);
            double duration = calculateOvertimeDays(dateList, startTime, endTime, normalWorkRangeList,
                    standardWorkMinutesMap);
            List<String> overtimeDateList = listOvertimeDate(dateList, startTime, endTime, normalWorkRangeList);

            Wo wo = new Wo();
            wo.setTotalDays(dateList.size());
            wo.setDuration(new BigDecimal(duration).setScale(1, RoundingMode.HALF_UP).doubleValue());
            wo.setDurationMinutes(durationMinutes);
            wo.setOvertimeDateList(overtimeDateList);
            wo.setRestDateList(restDateList);
            result.setData(wo);
            return result;
        }
    }

    private List<TimeRange> listNormalWorkRange(Business business, String personDn, Date startDate, Date endDate)
            throws Exception {
        List<String> dateList = AttendanceV2RestDayHelper.listDateRange(startDate, endDate);
        List<String> restDateList = AttendanceV2RestDayHelper.listRestDate(business, personDn, dateList);
        List<TimeRange> rangeList = new ArrayList<>();
        for (String date : dateList) {
            if (restDateList.contains(date)) {
                continue;
            }
            WoGroupShift woGroupShift = business.getAttendanceV2ManagerFactory().getGroupShiftByPersonDate(personDn,
                    date);
            AttendanceV2Shift shift = woGroupShift.getShift();
            if (shift == null || shift.getProperties() == null || shift.getProperties().getTimeList().isEmpty()) {
                rangeList.add(buildDayRange(date));
                continue;
            }
            for (AttendanceV2ShiftCheckTime checkTime : shift.getProperties().getTimeList()) {
                for (AttendanceV2ShiftWorkTimeHelper.TimeRange range : AttendanceV2ShiftWorkTimeHelper
                        .listWorkRange(date, checkTime)) {
                    rangeList.add(new TimeRange(range.getStart(), range.getEnd()));
                }
            }
        }
        return mergeRange(rangeList);
    }

    static long calculateOvertimeMinutes(Date startTime, Date endTime, List<TimeRange> normalWorkRangeList) {
        long totalMinutes = rangeMinutes(startTime, endTime);
        long normalMinutes = 0;
        for (TimeRange range : mergeRange(normalWorkRangeList)) {
            normalMinutes += overlapMinutes(startTime, endTime, range.getStart(), range.getEnd());
        }
        return Math.max(0, totalMinutes - normalMinutes);
    }

    static double calculateOvertimeDays(List<String> dateList, Date startTime, Date endTime,
            List<TimeRange> normalWorkRangeList, Map<String, Long> standardWorkMinutesMap) throws Exception {
        double duration = 0.0;
        List<TimeRange> mergedRangeList = mergeRange(normalWorkRangeList);
        for (String date : dateList) {
            long standardMinutes = standardWorkMinutesMap == null ? 0L
                    : standardWorkMinutesMap.getOrDefault(date, 0L);
            if (standardMinutes <= 0) {
                continue;
            }
            long overtimeMinutes = overtimeMinutesInDate(date, startTime, endTime, mergedRangeList);
            duration += overtimeMinutes * 1.0 / standardMinutes;
        }
        return duration;
    }

    static List<String> listOvertimeDate(List<String> dateList, Date startTime, Date endTime,
            List<TimeRange> normalWorkRangeList) throws Exception {
        List<String> overtimeDateList = new ArrayList<>();
        List<TimeRange> mergedRangeList = mergeRange(normalWorkRangeList);
        for (String date : dateList) {
            if (overtimeMinutesInDate(date, startTime, endTime, mergedRangeList) > 0) {
                overtimeDateList.add(date);
            }
        }
        return overtimeDateList;
    }

    private Map<String, Long> mapStandardWorkMinutes(Business business, String personDn, List<String> dateList,
            List<String> restDateList) throws Exception {
        Map<String, Long> map = new HashMap<>();
        for (String date : dateList) {
            long minutes = standardWorkMinutes(business, personDn, date);
            if (minutes <= 0 || restDateList.contains(date)) {
                minutes = nearestPastWorkdayStandardMinutes(business, personDn, date);
            }
            map.put(date, minutes);
        }
        return map;
    }

    private long nearestPastWorkdayStandardMinutes(Business business, String personDn, String date) throws Exception {
        Date cursor = DateTools.addDay(DateTools.parse(date, DateTools.format_yyyyMMdd), -1);
        for (int i = 0; i < 366; i++) {
            String cursorDate = DateTools.format(cursor, DateTools.format_yyyyMMdd);
            if (!AttendanceV2RestDayHelper.isRestDay(business, personDn, cursorDate)) {
                long minutes = standardWorkMinutes(business, personDn, cursorDate);
                if (minutes > 0) {
                    return minutes;
                }
            }
            cursor = DateTools.addDay(cursor, -1);
        }
        return 0;
    }

    private long standardWorkMinutes(Business business, String personDn, String date) throws Exception {
        WoGroupShift woGroupShift = business.getAttendanceV2ManagerFactory().getGroupShiftByPersonDate(personDn,
                date);
        if (woGroupShift == null) {
            return 0;
        }
        AttendanceV2Shift shift = woGroupShift.getShift();
        if (shift == null || shift.getProperties() == null || shift.getProperties().getTimeList().isEmpty()) {
            return 0;
        }
        long calculatedMinutes = AttendanceV2ShiftWorkTimeHelper.shiftWorkMinutes(shift.getProperties());
        return calculatedMinutes > 0 ? calculatedMinutes : shift.getWorkTime();
    }

    private static long overtimeMinutesInDate(String date, Date startTime, Date endTime,
            List<TimeRange> mergedRangeList) throws Exception {
        TimeRange dayRange = buildDayRange(date);
        long dayMinutes = overlapMinutes(startTime, endTime, dayRange.getStart(), dayRange.getEnd());
        long normalMinutes = 0;
        for (TimeRange range : mergedRangeList) {
            normalMinutes += overlapMinutes(startTime, endTime, maxDate(dayRange.getStart(), range.getStart()),
                    minDate(dayRange.getEnd(), range.getEnd()));
        }
        return Math.max(0, dayMinutes - normalMinutes);
    }

    private static TimeRange buildDayRange(String date) throws Exception {
        Date dayStart = DateTools.parse(date + " 00:00:00", DateTools.format_yyyyMMddHHmmss);
        return new TimeRange(dayStart, DateTools.addDay(dayStart, 1));
    }

    static List<TimeRange> mergeRange(List<TimeRange> rangeList) {
        List<TimeRange> mergedList = new ArrayList<>();
        if (rangeList == null || rangeList.isEmpty()) {
            return mergedList;
        }
        List<TimeRange> sortedList = new ArrayList<>(rangeList);
        sortedList.sort(Comparator.comparing(TimeRange::getStart));
        for (TimeRange range : sortedList) {
            if (mergedList.isEmpty()) {
                mergedList.add(range);
                continue;
            }
            TimeRange last = mergedList.get(mergedList.size() - 1);
            if (range.getStart().after(last.getEnd())) {
                mergedList.add(range);
            } else if (range.getEnd().after(last.getEnd())) {
                last.setEnd(range.getEnd());
            }
        }
        return mergedList;
    }

    private static long rangeMinutes(Date startTime, Date endTime) {
        if (endTime == null || startTime == null || !endTime.after(startTime)) {
            return 0;
        }
        return (endTime.getTime() - startTime.getTime()) / (60 * 1000);
    }

    private static long overlapMinutes(Date startTime, Date endTime, Date rangeStart, Date rangeEnd) {
        long start = Math.max(startTime.getTime(), rangeStart.getTime());
        long end = Math.min(endTime.getTime(), rangeEnd.getTime());
        if (end <= start) {
            return 0;
        }
        return (end - start) / (60 * 1000);
    }

    private static Date maxDate(Date d1, Date d2) {
        return d1.after(d2) ? d1 : d2;
    }

    private static Date minDate(Date d1, Date d2) {
        return d1.before(d2) ? d1 : d2;
    }

    static class TimeRange {

        private Date start;

        private Date end;

        TimeRange(Date start, Date end) {
            this.start = start;
            this.end = end;
        }

        Date getStart() {
            return start;
        }

        Date getEnd() {
            return end;
        }

        void setEnd(Date end) {
            this.end = end;
        }
    }

    public static class Wi extends GsonPropertyObject {

        private static final long serialVersionUID = -2353341673676674384L;

        @FieldDescribe("人员标识")
        private String person;

        @FieldDescribe("开始时间，yyyy-MM-dd HH:mm:ss")
        private Date startTime;

        @FieldDescribe("结束时间，yyyy-MM-dd HH:mm:ss")
        private Date endTime;



        public String getPerson() {
            return person;
        }

        public void setPerson(String person) {
            this.person = person;
        }

        public Date getStartTime() {
            return startTime;
        }

        public void setStartTime(Date startTime) {
            this.startTime = startTime;
        }

        public Date getEndTime() {
            return endTime;
        }

        public void setEndTime(Date endTime) {
            this.endTime = endTime;
        }
    }

    public static class Wo extends GsonPropertyObject {

        private static final long serialVersionUID = 8498242197758291608L;

        @FieldDescribe("自然日天数")
        private Integer totalDays = 0;

        @FieldDescribe("实际加班天数")
        private Double duration = 0.0;

        @FieldDescribe("实际加班分钟数")
        private Long durationMinutes = 0L;

        @FieldDescribe("实际加班的日期列表，yyyy-MM-dd")
        private List<String> overtimeDateList = new ArrayList<>();

        @FieldDescribe("休息日的日期列表，yyyy-MM-dd")
        private List<String> restDateList = new ArrayList<>();

        public Integer getTotalDays() {
            return totalDays;
        }

        public void setTotalDays(Integer totalDays) {
            this.totalDays = totalDays;
        }

        public Double getDuration() {
            return duration;
        }

        public void setDuration(Double duration) {
            this.duration = duration;
        }

        public Long getDurationMinutes() {
            return durationMinutes;
        }

        public void setDurationMinutes(Long durationMinutes) {
            this.durationMinutes = durationMinutes;
        }

        public List<String> getOvertimeDateList() {
            return overtimeDateList;
        }

        public void setOvertimeDateList(List<String> overtimeDateList) {
            this.overtimeDateList = overtimeDateList;
        }

        public List<String> getRestDateList() {
            return restDateList;
        }

        public void setRestDateList(List<String> restDateList) {
            this.restDateList = restDateList;
        }
    }
}
