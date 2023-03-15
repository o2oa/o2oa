package com.x.attendance.assemble.control.jaxrs.v2.detail;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.detail.model.StatisticWi;
import com.x.attendance.assemble.control.jaxrs.v2.detail.model.StatisticWo;
import com.x.attendance.entity.v2.AttendanceV2Detail;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

abstract class BaseAction extends StandardJaxrsAction {


    /**
     * 统计数据
     *
     * @param wi
     * @param userList
     * @param business
     * @param wos
     * @throws Exception
     */
    protected void statisticDetail(StatisticWi wi, List<String> userList, Business business, List<StatisticWo> wos) throws Exception {
        List<String> newList = userList.stream().distinct().collect(Collectors.toList()); // 去重
        for (String person : newList) {
            StatisticWo wo = new StatisticWo();
            wo.setUserId(person); //
            List<AttendanceV2Detail> list = business.getAttendanceV2ManagerFactory().listDetailWithPersonAndStartEndDate(person, wi.getStartDate(), wi.getEndDate());
            if (list != null && !list.isEmpty()) {
                Long workTimeDuration = 0L;
                Integer attendance = 0;
                Integer rest = 0;
                Integer absenteeismDays = 0;
                Integer lateTimes = 0;
                Integer leaveEarlierTimes = 0;
                Integer absenceTimes = 0;
                int workDayCount = 0;
                for (AttendanceV2Detail attendanceV2Detail : list) {
                    if (attendanceV2Detail.getWorkDay()) {
                        workDayCount += 1; //工作日加1
                    }
                    if (attendanceV2Detail.getWorkTimeDuration() > 0) {
                        workTimeDuration += attendanceV2Detail.getWorkTimeDuration();
                    }
                    if (attendanceV2Detail.getAttendance() > 0) {
                        attendance += attendanceV2Detail.getAttendance();
                    }
                    if (attendanceV2Detail.getRest() > 0) {
                        rest += attendanceV2Detail.getRest();
                    }
                    if (attendanceV2Detail.getAbsenteeismDays() > 0) {
                        absenteeismDays += attendanceV2Detail.getAbsenteeismDays();
                    }
                    if (attendanceV2Detail.getLateTimes() > 0) {
                        lateTimes += attendanceV2Detail.getLateTimes();
                    }
                    if (attendanceV2Detail.getLeaveEarlierTimes() > 0) {
                        leaveEarlierTimes += attendanceV2Detail.getLeaveEarlierTimes();
                    }
                    if (attendanceV2Detail.getOnDutyAbsenceTimes() > 0) {
                        absenceTimes += attendanceV2Detail.getOnDutyAbsenceTimes();
                    }
                    if (attendanceV2Detail.getOffDutyAbsenceTimes() > 0) {
                        absenceTimes += attendanceV2Detail.getOffDutyAbsenceTimes();
                    }
                }
                if (workDayCount > 0) {
                    DecimalFormat df = new DecimalFormat("0.0");
                    wo.setAverageWorkTimeDuration(df.format(((float) workTimeDuration.intValue() / workDayCount) / 60));
                }
                wo.setWorkTimeDuration(workTimeDuration);
                wo.setAttendance(attendance);
                wo.setRest(rest);
                wo.setAbsenteeismDays(absenteeismDays);
                wo.setLateTimes(lateTimes);
                wo.setLeaveEarlierTimes(leaveEarlierTimes);
                wo.setAbsenceTimes(absenceTimes);
            }
            wos.add(wo);
        }
    }


}
