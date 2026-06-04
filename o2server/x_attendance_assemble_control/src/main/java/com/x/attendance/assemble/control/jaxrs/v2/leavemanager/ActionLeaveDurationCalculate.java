package com.x.attendance.assemble.control.jaxrs.v2.leavemanager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.AttendanceV2RestDayHelper;
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

public class ActionLeaveDurationCalculate extends BaseAction {

    ActionResult<Wo> execute(JsonElement jsonElement) throws Exception {
        ActionResult<Wo> result = new ActionResult<>();
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            if (StringUtils.isEmpty(wi.getPerson())) {
                throw new ExceptionEmptyParameter("人员标识");
            }
            Date startTime = wi.getStartTime();
            Date endTime = wi.getEndTime();
            if (startTime == null && StringUtils.isNotEmpty(wi.getStartDate())) {
                startTime = DateTools.parse(wi.getStartDate(), DateTools.format_yyyyMMdd);
            }
            if (endTime == null && StringUtils.isNotEmpty(wi.getEndDate())) {
                endTime = DateTools.addDay(DateTools.parse(wi.getEndDate(), DateTools.format_yyyyMMdd), 1);
            }
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
            List<String> dateList = AttendanceV2RestDayHelper.listDateRange(startDate, endDate);
            List<String> restDateList = AttendanceV2RestDayHelper.listRestDate(business, person.getDistinguishedName(), dateList);
            List<String> workDateList = dateList.stream().filter(date -> !restDateList.contains(date))
                    .collect(Collectors.toList());
            List<String> leaveDateList = new ArrayList<>();
            long durationMinutes = 0;
            double duration = 0.0;
            for (String date : workDateList) {
                DayDuration dayDuration = calculateDayDuration(business, person.getDistinguishedName(), date, startTime, endTime);
                if (dayDuration.getDurationMinutes() > 0) {
                    leaveDateList.add(date);
                }
                durationMinutes += dayDuration.getDurationMinutes();
                duration += dayDuration.getDuration();
            }

            Wo wo = new Wo();
            wo.setTotalDays(dateList.size());
            wo.setDuration(new BigDecimal(duration).setScale(1, RoundingMode.HALF_UP).doubleValue());
            wo.setDurationMinutes(durationMinutes);
            wo.setLeaveDateList(leaveDateList);
            wo.setRestDateList(restDateList);
            result.setData(wo);
            return result;
        }
    }

    private DayDuration calculateDayDuration(Business business, String personDn, String date, Date startTime,
            Date endTime) throws Exception {
        WoGroupShift woGroupShift = business.getAttendanceV2ManagerFactory().getGroupShiftByPersonDate(personDn, date);
        AttendanceV2Shift shift = woGroupShift.getShift();
        if (shift == null || shift.getProperties() == null || shift.getProperties().getTimeList().isEmpty()) {
            return calculateDayDurationWithoutShift(date, startTime, endTime);
        }
        long shiftWorkMinutes = shift.getWorkTime();
        long calculatedWorkMinutes = 0;
        long durationMinutes = 0;
        for (AttendanceV2ShiftCheckTime checkTime : shift.getProperties().getTimeList()) {
            Date onDuty = DateTools.parse(date + " " + checkTime.getOnDutyTime(), DateTools.format_yyyyMMddHHmm);
            Date offDuty = DateTools.parse(date + " " + checkTime.getOffDutyTime(), DateTools.format_yyyyMMddHHmm);
            if (BooleanUtils.isTrue(checkTime.getOffDutyNextDay())) {
                offDuty = DateTools.addDay(offDuty, 1);
            }
            calculatedWorkMinutes += standardMinutes(onDuty, offDuty);
            durationMinutes += overlapMinutes(startTime, endTime, onDuty, offDuty);
        }
        long standardMinutes = shiftWorkMinutes > 0 ? shiftWorkMinutes : calculatedWorkMinutes;
        double duration = standardMinutes > 0 ? durationMinutes * 1.0 / standardMinutes : 0.0;
        return new DayDuration(durationMinutes, duration);
    }

    private DayDuration calculateDayDurationWithoutShift(String date, Date startTime, Date endTime) throws Exception {
        Date dayStart = DateTools.parse(date + " 00:00:00", DateTools.format_yyyyMMddHHmmss);
        Date dayEnd = DateTools.addDay(dayStart, 1);
        long durationMinutes = overlapMinutes(startTime, endTime, dayStart, dayEnd);
        double duration = durationMinutes > 0 ? durationMinutes * 1.0 / (24 * 60) : 0.0;
        return new DayDuration(durationMinutes, duration);
    }

    private long standardMinutes(Date onDuty, Date offDuty) {
        return Math.max(0, (offDuty.getTime() - onDuty.getTime()) / (60 * 1000));
    }

    private long overlapMinutes(Date startTime, Date endTime, Date rangeStart, Date rangeEnd) {
        long start = Math.max(startTime.getTime(), rangeStart.getTime());
        long end = Math.min(endTime.getTime(), rangeEnd.getTime());
        if (end <= start) {
            return 0;
        }
        return (end - start) / (60 * 1000);
    }

    private static class DayDuration {

        private long durationMinutes;

        private double duration;

        private DayDuration(long durationMinutes, double duration) {
            this.durationMinutes = durationMinutes;
            this.duration = duration;
        }

        private long getDurationMinutes() {
            return durationMinutes;
        }

        private double getDuration() {
            return duration;
        }
    }

    public static class Wi extends GsonPropertyObject {

        private static final long serialVersionUID = 3972136115624287303L;

        @FieldDescribe("人员标识")
        private String person;

        @FieldDescribe("开始时间，yyyy-MM-dd HH:mm:ss")
        private Date startTime;

        @FieldDescribe("结束时间，yyyy-MM-dd HH:mm:ss")
        private Date endTime;

        @FieldDescribe("开始日期，yyyy-MM-dd，兼容旧参数")
        private String startDate;

        @FieldDescribe("结束日期，yyyy-MM-dd，兼容旧参数")
        private String endDate;

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

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }
    }

    public static class Wo extends GsonPropertyObject {

        private static final long serialVersionUID = 2373093028785508444L;

        @FieldDescribe("自然日天数")
        private Integer totalDays = 0;

        @FieldDescribe("实际请假天数")
        private Double duration = 0.0;

        @FieldDescribe("实际请假分钟数")
        private Long durationMinutes = 0L;

        @FieldDescribe("实际请假的日期列表，yyyy-MM-dd")
        private List<String> leaveDateList = new ArrayList<>();

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

        public List<String> getLeaveDateList() {
            return leaveDateList;
        }

        public void setLeaveDateList(List<String> leaveDateList) {
            this.leaveDateList = leaveDateList;
        }

        public List<String> getRestDateList() {
            return restDateList;
        }

        public void setRestDateList(List<String> restDateList) {
            this.restDateList = restDateList;
        }
    }
}
