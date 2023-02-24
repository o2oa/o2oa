package com.x.attendance.assemble.control.schedule.v2;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.v2.*;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.base.core.project.tools.DateTools;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 队列处理人员某一天的考勤打卡数据，生成考勤详细信息
 * Created by fancyLou on 2023/2/24.
 * Copyright © 2023 O2. All rights reserved.
 */
public class QueueAttendanceV2Detail extends AbstractQueue<QueueAttendanceV2DetailModel> {


    private static final Logger logger = LoggerFactory.getLogger(QueueAttendanceV2Detail.class);


    @Override
    protected void execute(QueueAttendanceV2DetailModel model) throws Exception {
        if (model != null && StringUtils.isNotEmpty(model.getPerson()) && StringUtils.isNotEmpty(model.getDate())) {
            if (logger.isDebugEnabled()) {
                logger.debug("考勤数据处理 ==== 处理人员 {} 处理日期 {} ===================", model.getPerson(), model.getDate());
            }
            try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
                Business business = new Business(emc);
                List<AttendanceV2Group> groups = business.getAttendanceV2ManagerFactory().listGroupWithPerson(model.getPerson());
                if (groups == null || groups.isEmpty()) {
                    throw new ExceptionQueueAttendanceV2Detail(model.getPerson() + "没有对应的考勤组!");
                }
                AttendanceV2Group group = groups.get(0);
                // 查询班次对象
                AttendanceV2Shift shift = emc.find(group.getShiftId(), AttendanceV2Shift.class);
                if (shift == null) {
                    throw new ExceptionQueueAttendanceV2Detail(model.getPerson() + "没有对应的班次信息!");
                }
                List<AttendanceV2ShiftCheckTime> timeList = shift.getProperties().getTimeList();
                if (timeList == null || timeList.isEmpty()) {
                    throw new ExceptionQueueAttendanceV2Detail(shift.getShiftName() + "没有对应的上下班打卡时间!");
                }
                // 判断休息日还是工作日
                if (StringUtils.isEmpty(group.getWorkDateList())) {
                    throw new ExceptionQueueAttendanceV2Detail("考勤工作日设置为空！");
                }
                String[] workDayList = group.getWorkDateList().split(",");
                List<Integer> dayList = Arrays.stream(workDayList).map(Integer::parseInt).collect(Collectors.toList());
                Date date = DateTools.parse(model.getDate(), DateTools.format_yyyyMMdd);
                int day = dayForWeek(date);
                //TODO 还有节假日管理
                boolean isWorkDay = dayList.contains(day);
                if (group.getNoNeedCheckInDateList() != null && !group.getNoNeedCheckInDateList().isEmpty()) {
                    if (group.getNoNeedCheckInDateList().contains(model.getDate())) {
                        isWorkDay = false; // 无需打卡就是休息日
                    }
                }
                if (group.getRequiredCheckInDateList() != null && !group.getRequiredCheckInDateList().isEmpty()) {
                    if (group.getRequiredCheckInDateList().contains(model.getDate())) {
                        isWorkDay = true; // 必须打卡就是休息日
                    }
                }


                // 查询打卡数据
                List<AttendanceV2CheckInRecord> recordList = business.getAttendanceV2ManagerFactory().listRecordWithPersonAndDate(model.getPerson(), model.getDate());
                // 工作日 没有数据 生成未打卡数据
                if (recordList == null || recordList.isEmpty()) {
                    recordList = new ArrayList<>();
                    for (AttendanceV2ShiftCheckTime shiftCheckTime : timeList) {
                        // 上班打卡
                        AttendanceV2CheckInRecord onDutyRecord = saveNoCheckInRecord(emc, model.getPerson(), AttendanceV2CheckInRecord.OnDuty, group, shift, model.getDate(),
                                shiftCheckTime.getOnDutyTime(), shiftCheckTime.getOnDutyTimeBeforeLimit(), shiftCheckTime.getOnDutyTimeAfterLimit());
                        recordList.add(onDutyRecord);
                        // 下班打卡
                        AttendanceV2CheckInRecord offDutyRecord = saveNoCheckInRecord(emc, model.getPerson(), AttendanceV2CheckInRecord.OffDuty, group, shift, model.getDate(),
                                shiftCheckTime.getOffDutyTime(), shiftCheckTime.getOffDutyTimeBeforeLimit(), shiftCheckTime.getOffDutyTimeAfterLimit());
                        recordList.add(offDutyRecord);
                    }
                }
                // 处理未打卡的预保存数据
                for (AttendanceV2CheckInRecord record : recordList) {
                    if (record.getCheckInResult().equals(AttendanceV2CheckInRecord.CHECKIN_RESULT_PreCheckIn)) {
                        updateRecord2NoCheckIn(emc, record);
                    }
                }

                // 迟到
                List<AttendanceV2CheckInRecord> late = recordList.stream().filter((r) -> r.getCheckInResult().equals(AttendanceV2CheckInRecord.CHECKIN_RESULT_Late)).collect(Collectors.toList());
                long lateMinute = 0;
                if (!late.isEmpty()) {
                    for (AttendanceV2CheckInRecord record : late) {
                        Date dutyTime = DateTools.parse(model.getDate() + " " + record.getPreDutyTime(), DateTools.format_yyyyMMddHHmm);
                        long time = record.getRecordDate().getTime() - dutyTime.getTime();
                        lateMinute += (time > 0 ? time : -time) / 1000 / 60;
                    }
                }
                // 早退
                List<AttendanceV2CheckInRecord> early = recordList.stream().filter((r) -> r.getCheckInResult().equals(AttendanceV2CheckInRecord.CHECKIN_RESULT_Early)).collect(Collectors.toList());
                long earlyMinute = 0;
                if (!early.isEmpty()) {
                    for (AttendanceV2CheckInRecord record : early) {
                        Date dutyTime = DateTools.parse(model.getDate() + " " + record.getPreDutyTime(), DateTools.format_yyyyMMddHHmm);
                        long time = dutyTime.getTime() - record.getRecordDate().getTime();
                        earlyMinute += (time > 0 ? time : -time) / 1000 / 60;
                    }
                }

                // 上班打卡
                List<AttendanceV2CheckInRecord> onDutyList = recordList.stream().filter(
                                (r) -> r.getCheckInType().equals(AttendanceV2CheckInRecord.OnDuty) && (!r.getCheckInResult().equals(AttendanceV2CheckInRecord.CHECKIN_RESULT_NotSigned) && !r.getCheckInResult().equals(AttendanceV2CheckInRecord.CHECKIN_RESULT_PreCheckIn)))
                        .sorted(Comparator.comparing(AttendanceV2CheckInRecord::getRecordDate)).collect(Collectors.toList());
                // 下班打卡
                List<AttendanceV2CheckInRecord> offDutyList = recordList.stream().filter(
                                (r) -> r.getCheckInType().equals(AttendanceV2CheckInRecord.OffDuty) && (!r.getCheckInResult().equals(AttendanceV2CheckInRecord.CHECKIN_RESULT_NotSigned) && !r.getCheckInResult().equals(AttendanceV2CheckInRecord.CHECKIN_RESULT_PreCheckIn)))
                        .sorted(Comparator.comparing(AttendanceV2CheckInRecord::getRecordDate)).collect(Collectors.toList());
                // 工作时长
                long workTimeDuration = 0;
                for (int i = 0; i < onDutyList.size(); i++) {
                    AttendanceV2CheckInRecord onDuty = onDutyList.get(i);
                    if (offDutyList.size() - 1 >= i) {
                        AttendanceV2CheckInRecord offDuty = offDutyList.get(i);
                        workTimeDuration += (offDuty.getRecordDate().getTime() - onDuty.getRecordDate().getTime()) / 1000 / 60;
                    }
                }

                // 考勤对象
                List<AttendanceV2Detail> details = business.getAttendanceV2ManagerFactory().listDetailWithPersonAndDate(model.getPerson(), model.getDate());
                AttendanceV2Detail v2Detail;
                if (details != null && !details.isEmpty()) {
                    v2Detail = details.get(0);
                } else {
                    v2Detail = new AttendanceV2Detail();
                }
                v2Detail.setUserId(model.getPerson());
                v2Detail.setYearString(model.getDate().substring(0, 4));
                v2Detail.setMonthString(model.getDate().substring(5, 7));
                v2Detail.setRecordDateString(model.getDate());
                v2Detail.setWorkday(isWorkDay);
                v2Detail.setLateTimeDuration(lateMinute);
                v2Detail.setLateTimes(lateMinute > 0 ? 1 : 0);
                v2Detail.setLeaveEarlierTimeDuration(earlyMinute);
                v2Detail.setLeaveEarlierTimes(earlyMinute > 0 ? 1 : 0);
                v2Detail.setWorkTimeDuration(workTimeDuration);
                // 出勤天数
                List<AttendanceV2CheckInRecord> attendanceList = recordList.stream().filter(
                                (r) -> (!r.getCheckInResult().equals(AttendanceV2CheckInRecord.CHECKIN_RESULT_NotSigned) && !r.getCheckInResult().equals(AttendanceV2CheckInRecord.CHECKIN_RESULT_PreCheckIn)))
                        .collect(Collectors.toList());
                v2Detail.setAttendance(attendanceList.size() > 0 ? 1 : 0);
                if (!isWorkDay) {
                    v2Detail.setRest(1);
                } else {
                    // 旷工
                    v2Detail.setAbsenteeismDays(workTimeDuration == 0 ? 1 : 0);
                    List<AttendanceV2CheckInRecord> noCheckInOnDutyList = recordList.stream().filter(
                                    (r) -> r.getCheckInType().equals(AttendanceV2CheckInRecord.OnDuty) && r.getCheckInResult().equals(AttendanceV2CheckInRecord.CHECKIN_RESULT_NotSigned))
                            .collect(Collectors.toList());
                    v2Detail.setOnDutyAbsenceTimes(noCheckInOnDutyList.size());
                    List<AttendanceV2CheckInRecord> noCheckInOffDutyList = recordList.stream().filter(
                                    (r) -> r.getCheckInType().equals(AttendanceV2CheckInRecord.OffDuty) && r.getCheckInResult().equals(AttendanceV2CheckInRecord.CHECKIN_RESULT_NotSigned))
                            .collect(Collectors.toList());
                    v2Detail.setOffDutyAbsenceTimes(noCheckInOffDutyList.size());
                }
                v2Detail.setRecordIdList(recordList.stream().map(AttendanceV2CheckInRecord::getId).collect(Collectors.toList()));
                v2Detail.setGroupId(group.getId());
                v2Detail.setGroupName(group.getGroupName());
                v2Detail.setShiftId(shift.getId());
                v2Detail.setShiftName(shift.getShiftName());
                emc.beginTransaction(AttendanceV2Detail.class);
                emc.persist(v2Detail, CheckPersistType.all);
                emc.commit();
                if (logger.isDebugEnabled()) {
                    logger.debug("考勤数据处理完成, {}", v2Detail.toString());
                }
            }
        }
    }

    /**
     * 判断当前日期是星期几
     *
     * @param dateTime 修要判断的时间
     * @return 0-6 代表 星期天-星期六
     * @Exception 发生异常
     */
    private int dayForWeek(Date dateTime) throws Exception {
        Calendar c = Calendar.getInstance();
        c.setTime(dateTime);
        return c.get(Calendar.DAY_OF_WEEK) - 1;

    }


    // 工作日 预存储数据更新未未打卡
    private void updateRecord2NoCheckIn(EntityManagerContainer emc, AttendanceV2CheckInRecord record) throws Exception {
        record.setCheckInResult(AttendanceV2CheckInRecord.CHECKIN_RESULT_NotSigned);
        record.setSourceType(AttendanceV2CheckInRecord.SOURCE_TYPE_AUTO_CHECK);
        record.setSourceDevice("其他");
        record.setDescription("系统生成，未打卡记录");
        emc.beginTransaction(AttendanceV2CheckInRecord.class);
        emc.persist(record, CheckPersistType.all);
        emc.commit();
    }

    // 未打卡保存
    private AttendanceV2CheckInRecord saveNoCheckInRecord(EntityManagerContainer emc, String person, String dutyType,
                                                          AttendanceV2Group group, AttendanceV2Shift shift, String today,
                                                          String dutyTime, String dutyTimeBeforeLimit, String dutyTimeAfterLimit) throws Exception {
        AttendanceV2CheckInRecord noCheckRecord = new AttendanceV2CheckInRecord();
        noCheckRecord.setCheckInType(dutyType);
        noCheckRecord.setCheckInResult(AttendanceV2CheckInRecord.CHECKIN_RESULT_NotSigned);
        noCheckRecord.setUserId(person);
        // 打卡时间
        Date onDutyTime = DateTools.parse(today + " " + dutyTime, DateTools.format_yyyyMMddHHmm);
        noCheckRecord.setRecordDate(onDutyTime);
        noCheckRecord.setRecordDateString(today);
        noCheckRecord.setPreDutyTime(dutyTime);
        noCheckRecord.setPreDutyTimeBeforeLimit(dutyTimeBeforeLimit);
        noCheckRecord.setPreDutyTimeAfterLimit(dutyTimeAfterLimit);
        noCheckRecord.setSourceType(AttendanceV2CheckInRecord.SOURCE_TYPE_AUTO_CHECK);
        noCheckRecord.setSourceDevice("其他");
        noCheckRecord.setDescription("系统生成，未打卡记录");
        noCheckRecord.setGroupId(group.getId());
        noCheckRecord.setGroupName(group.getGroupName());
        noCheckRecord.setShiftId(shift.getId());
        noCheckRecord.setShiftName(shift.getShiftName());
        emc.beginTransaction(AttendanceV2CheckInRecord.class);
        emc.persist(noCheckRecord, CheckPersistType.all);
        emc.commit();
        return noCheckRecord;
    }
}
