package com.x.attendance.assemble.control.jaxrs.v2.detail;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.detail.model.DetailWo;
import com.x.attendance.assemble.control.jaxrs.v2.detail.model.RecordWo;
import com.x.attendance.assemble.control.jaxrs.v2.detail.model.StatisticWi;
import com.x.attendance.assemble.control.jaxrs.v2.detail.model.StatisticWo;
import com.x.attendance.entity.v2.AttendanceV2AppealInfo;
import com.x.attendance.entity.v2.AttendanceV2CheckInRecord;
import com.x.attendance.entity.v2.AttendanceV2Detail;
import com.x.attendance.entity.v2.AttendanceV2LeaveData;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;

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
            List<DetailWo> detailWos = new ArrayList<>();
            if (list != null && !list.isEmpty()) {
                Long workTimeDuration = 0L;
                int attendance = 0;
                int rest = 0;
                int absenteeismDays = 0;
                int lateTimes = 0;
                int leaveEarlierTimes = 0;
                int absenceTimes = 0;
                // int workDayCount = 0;
                int fieldWorkTimes = 0;
                int leaveDays = 0;
                int appealNums = 0;
                for (AttendanceV2Detail attendanceV2Detail : list) {
                    // if (BooleanUtils.isTrue( attendanceV2Detail.getWorkDay()) && (attendanceV2Detail.getLeaveDays() == null || attendanceV2Detail.getLeaveDays() < 1)) {
                    //     workDayCount += 1; //工作日加1
                    // }
                    if (attendanceV2Detail.getWorkTimeDuration() != null && attendanceV2Detail.getWorkTimeDuration() > 0) {
                        workTimeDuration += attendanceV2Detail.getWorkTimeDuration();
                    }
                    if (attendanceV2Detail.getAttendance() != null && attendanceV2Detail.getAttendance() > 0) {
                        attendance += attendanceV2Detail.getAttendance();
                    }
                    if (attendanceV2Detail.getRest() != null && attendanceV2Detail.getRest() > 0) {
                        rest += attendanceV2Detail.getRest();
                    }
                    if (attendanceV2Detail.getAbsenteeismDays() != null && attendanceV2Detail.getAbsenteeismDays() > 0) {
                        absenteeismDays += attendanceV2Detail.getAbsenteeismDays();
                    }
                    if (attendanceV2Detail.getLateTimes() != null && attendanceV2Detail.getLateTimes() > 0) {
                        lateTimes += attendanceV2Detail.getLateTimes();
                    }
                    if (attendanceV2Detail.getLeaveEarlierTimes() != null && attendanceV2Detail.getLeaveEarlierTimes() > 0) {
                        leaveEarlierTimes += attendanceV2Detail.getLeaveEarlierTimes();
                    }
                    if (attendanceV2Detail.getOnDutyAbsenceTimes() != null  && attendanceV2Detail.getOnDutyAbsenceTimes() > 0) {
                        absenceTimes += attendanceV2Detail.getOnDutyAbsenceTimes();
                    }
                    if (attendanceV2Detail.getOffDutyAbsenceTimes() != null && attendanceV2Detail.getOffDutyAbsenceTimes() > 0) {
                        absenceTimes += attendanceV2Detail.getOffDutyAbsenceTimes();
                    }
                    if (attendanceV2Detail.getFieldWorkTimes() != null && attendanceV2Detail.getFieldWorkTimes() > 0) {
                        fieldWorkTimes += attendanceV2Detail.getFieldWorkTimes();
                    }
                    if (attendanceV2Detail.getLeaveDays() != null && attendanceV2Detail.getLeaveDays() > 0) {
                        leaveDays += attendanceV2Detail.getLeaveDays();
                    }
                    // 查询打卡记录 计算申诉数据
                    List<AttendanceV2CheckInRecord> recordList = business.getAttendanceV2ManagerFactory().listRecordWithPersonAndDate(person, attendanceV2Detail.getRecordDateString());
                    appealNums += (int) recordList.stream().filter((r) -> StringUtils.isNotEmpty(r.getAppealId())).count();
                    // 前端展现用的
                    DetailWo detailWo = DetailWo.copier.copy(attendanceV2Detail);
                    List<RecordWo> recordWos = RecordWo.copier.copy(recordList);
                    for (RecordWo recordWo : recordWos) {
                        try {
                            if (StringUtils.isNotEmpty(recordWo.getLeaveDataId())) {
                                AttendanceV2LeaveData leaveData = business.entityManagerContainer().find(recordWo.getLeaveDataId(), AttendanceV2LeaveData.class);
                                if (leaveData != null) {
                                    recordWo.setLeaveData(leaveData);
                                }
                            }
                            if (StringUtils.isNotEmpty(recordWo.getAppealId())) {
                                AttendanceV2AppealInfo appealData = business.entityManagerContainer().find(recordWo.getAppealId(), AttendanceV2AppealInfo.class);
                                if (appealData != null) {
                                    recordWo.setAppealData(appealData);
                                }
                            }
                        } catch (Exception ignore) {}
                    }
                    detailWo.setRecordList(recordWos);
                    detailWos.add(detailWo);
                }
                // 根据出勤天数平均 不是工作日
                if (attendance > 0) {
                    DecimalFormat df = new DecimalFormat("0.0");
                    wo.setAverageWorkTimeDuration(df.format(((float) workTimeDuration.intValue() / attendance) / 60));
                }
                wo.setWorkTimeDuration(workTimeDuration);
                wo.setAttendance(attendance);
                wo.setRest(rest);
                wo.setAbsenteeismDays(absenteeismDays);
                wo.setLateTimes(lateTimes);
                wo.setLeaveEarlierTimes(leaveEarlierTimes);
                wo.setAbsenceTimes(absenceTimes);
                wo.setFieldWorkTimes(fieldWorkTimes);
                wo.setLeaveDays(leaveDays);
                wo.setAppealNums(appealNums);
                wo.setDetailList(detailWos);
            }
            wos.add(wo);
        }
    }


}
