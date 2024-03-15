package com.x.attendance.assemble.control.schedule.v2;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.AttendanceV2Helper;
import com.x.attendance.assemble.control.jaxrs.v2.WoGroupShift;
import com.x.attendance.entity.v2.*;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.base.core.project.tools.DateTools;
import org.apache.commons.lang3.BooleanUtils;
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
        if (model == null || StringUtils.isEmpty(model.getPerson()) || StringUtils.isEmpty(model.getDate())) {
            logger.info("传入参数错误，无法处理数据");
            return;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("考勤数据处理 ==== 处理人员 {} 处理日期 {} ===================", model.getPerson(), model.getDate());
        }
        if (!AttendanceV2Helper.beforeToday(model.getDate())) {
            logger.info("日期不正确！");
            return;
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            // 人员查询所属考勤组
            List<AttendanceV2Group> groups = business.getAttendanceV2ManagerFactory()
                    .listGroupWithPerson(model.getPerson());
            if (groups == null || groups.isEmpty()) {
                throw new ExceptionQueueAttendanceV2Detail(model.getPerson() + "没有对应的考勤组!");
            }
            // 日期
            Date date = DateTools.parse(model.getDate(), DateTools.format_yyyyMMdd);
            // 周几 0-6 代表 星期天 - 星期六
            int day = DateTools.dayForWeekAttendanceV2(date);
            // 是否工作日
            boolean isWorkDay = false;
            // 查询打卡数据
            List<AttendanceV2CheckInRecord> recordList = business.getAttendanceV2ManagerFactory()
                    .listRecordWithPersonAndDate(model.getPerson(), model.getDate());
            AttendanceV2Group group = groups.get(0);
            AttendanceV2Shift shift = null; // 班次对象
            AttendanceV2Config config = null; // 配置对象
            List<AttendanceV2Config> configs = emc.listAll(AttendanceV2Config.class);
            if (configs != null && !configs.isEmpty()) {
                config = configs.get(0);
            }
            // 班次对象
            WoGroupShift woGroupShift = business.getAttendanceV2ManagerFactory()
                    .getGroupShiftByPersonDate(model.getPerson(), model.getDate());
            if (woGroupShift != null) {
                shift = woGroupShift.getShift();
            }
            if (group.getCheckType().equals(AttendanceV2Group.CHECKTYPE_Free)) { // 自由工时
                // 判断休息日还是工作日
                if (StringUtils.isEmpty(group.getWorkDateList())) {
                    throw new ExceptionQueueAttendanceV2Detail("考勤工作日设置为空！");
                }
                String[] workDayList = group.getWorkDateList().split(",");
                List<Integer> dayList = Arrays.stream(workDayList).map(Integer::parseInt).collect(Collectors.toList());
                // 是否工作日
                isWorkDay = dayList.contains(day);
                // 特殊节假日
                if (isWorkDay && AttendanceV2Helper.isSpecialRestDay(model.getDate(), group)) {
                    isWorkDay = false;
                }
            } else {
                if (shift != null) {
                    isWorkDay = true;
                    List<AttendanceV2ShiftCheckTime> timeList = shift.getProperties().getTimeList();
                    if (timeList == null || timeList.isEmpty()) {
                        throw new ExceptionQueueAttendanceV2Detail(shift.getShiftName() + "没有对应的上下班打卡时间!");
                    }
                    // 工作日 没有数据 生成未打卡数据
                    if (recordList == null || recordList.isEmpty()) {
                        recordList = new ArrayList<>();
                        for (AttendanceV2ShiftCheckTime shiftCheckTime : timeList) {
                            // 上班打卡
                            AttendanceV2CheckInRecord onDutyRecord = saveNoCheckInRecord(emc, model.getPerson(),
                                    AttendanceV2CheckInRecord.OnDuty, group, shift, model.getDate(),
                                    shiftCheckTime.getOnDutyTime(), shiftCheckTime.getOnDutyTimeBeforeLimit(),
                                    shiftCheckTime.getOnDutyTimeAfterLimit(), false);
                            recordList.add(onDutyRecord);
                            // 下班打卡
                            AttendanceV2CheckInRecord offDutyRecord = saveNoCheckInRecord(emc, model.getPerson(),
                                    AttendanceV2CheckInRecord.OffDuty, group, shift, model.getDate(),
                                    shiftCheckTime.getOffDutyTime(), shiftCheckTime.getOffDutyTimeBeforeLimit(),
                                    shiftCheckTime.getOffDutyTimeAfterLimit(),
                                    BooleanUtils.isTrue(shiftCheckTime.getOffDutyNextDay()));
                            recordList.add(offDutyRecord);
                        }
                    }
                }
            }
            // 如果没有数据，可能是自由工时 或者 休息日没有班次信息的情况下 只需要生成一条上班一条下班的打卡记录
            if (recordList == null || recordList.isEmpty()) {
                recordList = new ArrayList<>();
                // 上班打卡
                AttendanceV2CheckInRecord onDutyRecord = saveNoCheckInRecord(emc, model.getPerson(),
                        AttendanceV2CheckInRecord.OnDuty, group, null, model.getDate(),
                        null, null, null, false);
                recordList.add(onDutyRecord);
                // 下班打卡
                AttendanceV2CheckInRecord offDutyRecord = saveNoCheckInRecord(emc, model.getPerson(),
                        AttendanceV2CheckInRecord.OffDuty, group, null, model.getDate(),
                        null, null, null, false);
                recordList.add(offDutyRecord);
            }

            Date now = new Date();
            // 处理未打卡的预保存数据
            for (AttendanceV2CheckInRecord record : recordList) {
                // 未打卡数据 并且 打卡时间已经过了
                if (record.getCheckInResult().equals(AttendanceV2CheckInRecord.CHECKIN_RESULT_PreCheckIn)
                        && now.after(record.getRecordDate())) {
                    updateRecord2NoCheckIn(emc, record);
                }
            }

            long lateMinute = 0;
            long earlyMinute = 0;
            if (isWorkDay) {
                // 处理请假数据 异常打卡 是否是在请假的数据中
                for (AttendanceV2CheckInRecord record : recordList) {
                    if (!record.getCheckInResult().equals(AttendanceV2CheckInRecord.CHECKIN_RESULT_NORMAL)
                            && !record.getCheckInResult().equals(AttendanceV2CheckInRecord.CHECKIN_RESULT_PreCheckIn)) {
                        checkUpdateRecordIsLeave(emc, business, record);
                    }
                }

                // 迟到数据
                List<AttendanceV2CheckInRecord> late = recordList.stream()
                        .filter((r) -> (r.getCheckInResult().equals(AttendanceV2CheckInRecord.CHECKIN_RESULT_Late) || r.getCheckInResult().equals(AttendanceV2CheckInRecord.CHECKIN_RESULT_SeriousLate)) && StringUtils.isEmpty(r.getLeaveDataId()))
                        .collect(Collectors.toList());

                if (!late.isEmpty()) {
                    for (AttendanceV2CheckInRecord record : late) {
                        Date dutyTime = DateTools.parse(model.getDate() + " " + record.getPreDutyTime(),
                                DateTools.format_yyyyMMddHHmm);
                        long time = record.getRecordDate().getTime() - dutyTime.getTime();
                        lateMinute += (time > 0 ? time : -time) / 1000 / 60;
                    }
                }

                // 早退数据
                List<AttendanceV2CheckInRecord> early = recordList.stream()
                        .filter((r) -> r.getCheckInResult().equals(AttendanceV2CheckInRecord.CHECKIN_RESULT_Early) && StringUtils.isEmpty(r.getLeaveDataId()))
                        .collect(Collectors.toList());
                if (!early.isEmpty()) {
                    for (AttendanceV2CheckInRecord record : early) {
                        Date dutyTime = DateTools.parse(model.getDate() + " " + record.getPreDutyTime(),
                                DateTools.format_yyyyMMddHHmm);
                        long time = dutyTime.getTime() - record.getRecordDate().getTime();
                        earlyMinute += (time > 0 ? time : -time) / 1000 / 60;
                    }
                }
            }


            // 上班打卡
            List<AttendanceV2CheckInRecord> onDutyList = recordList.stream().filter(
                            (r) -> r.getCheckInType().equals(AttendanceV2CheckInRecord.OnDuty)
                                    && AttendanceV2Helper.isRecordAttendance(r))
                    .sorted(Comparator.comparing(AttendanceV2CheckInRecord::getRecordDate))
                    .collect(Collectors.toList());
            // 下班打卡
            List<AttendanceV2CheckInRecord> offDutyList = recordList.stream().filter(
                            (r) -> r.getCheckInType().equals(AttendanceV2CheckInRecord.OffDuty)
                                    && AttendanceV2Helper.isRecordAttendance(r))
                    .sorted(Comparator.comparing(AttendanceV2CheckInRecord::getRecordDate))
                    .collect(Collectors.toList());
            // 工作时长
            long workTimeDuration = 0;
            for (int i = 0; i < onDutyList.size(); i++) {
                AttendanceV2CheckInRecord onDuty = onDutyList.get(i);
                if (offDutyList.size() - 1 >= i) {
                    AttendanceV2CheckInRecord offDuty = offDutyList.get(i);
                    workTimeDuration += (offDuty.getRecordDate().getTime() - onDuty.getRecordDate().getTime()) / 1000
                            / 60;
                }
            }

            // 外勤打卡
            List<AttendanceV2CheckInRecord> fieldWorkList = recordList.stream()
                    .filter((r) -> r.getFieldWork() != null && BooleanUtils.isTrue(r.getFieldWork()))
                    .collect(Collectors.toList());

            // 有请假的列表
            List<AttendanceV2CheckInRecord> leaveList = recordList.stream()
                    .filter((r) -> StringUtils.isNotEmpty(r.getLeaveDataId())).collect(Collectors.toList());

            // 考勤对象
            List<AttendanceV2Detail> details = business.getAttendanceV2ManagerFactory()
                    .listDetailWithPersonAndDate(model.getPerson(), model.getDate());
            AttendanceV2Detail v2Detail;
            if (details != null && !details.isEmpty()) {
                v2Detail = details.get(0);
            } else {
                v2Detail = new AttendanceV2Detail();
            }
            v2Detail.setUserId(model.getPerson());
            v2Detail.setYearString(model.getDate().substring(0, 4));
            v2Detail.setMonthString(model.getDate().substring(5, 7));
            v2Detail.setRecordDateString(model.getDate()); // 日期
            v2Detail.setWorkDay(isWorkDay); // 是否工作日
            v2Detail.setRecordDay(day + ""); // 周几
            v2Detail.setLateTimeDuration(lateMinute);
            v2Detail.setLateTimes(lateMinute > 0 ? 1 : 0);
            v2Detail.setLeaveEarlierTimeDuration(earlyMinute);
            v2Detail.setLeaveEarlierTimes(earlyMinute > 0 ? 1 : 0);
            // 工作时长
            v2Detail.setWorkTimeDuration(workTimeDuration);
            // 出勤天数
            List<AttendanceV2CheckInRecord> attendanceList = recordList.stream().filter(
                            AttendanceV2Helper::isRecordAttendance)
                    .collect(Collectors.toList());
            v2Detail.setAttendance(!attendanceList.isEmpty() ? 1 : 0);
            if (!isWorkDay) {
                v2Detail.setRest(1);
                // 重新计算会导致数据错误 需要填入 0
                v2Detail.setLeaveDays(0);
                v2Detail.setAbsenteeismDays(0);
                v2Detail.setOnDutyAbsenceTimes(0);
                v2Detail.setOffDutyAbsenceTimes(0);
            } else {
                v2Detail.setRest(0);
                if (!leaveList.isEmpty()) { // 请假
                    v2Detail.setLeaveDays(1);
                    v2Detail.setAbsenteeismDays(0); // 重新计算会导致数据错误 需要填入 0
                } else {
                    v2Detail.setLeaveDays(0); // 重新计算会导致数据错误 需要填入 0
                    // 旷工 有正常打卡记录就不算旷工？
                    v2Detail.setAbsenteeismDays(!attendanceList.isEmpty() ? 0 : 1);
                }
                // 上班缺卡
                List<AttendanceV2CheckInRecord> noCheckInOnDutyList = recordList.stream().filter(
                                (r) -> r.getCheckInType().equals(AttendanceV2CheckInRecord.OnDuty)
                                        && AttendanceV2Helper.isRecordNotSign(r))
                        .collect(Collectors.toList());
                v2Detail.setOnDutyAbsenceTimes(noCheckInOnDutyList.size());
                // 下班缺卡
                List<AttendanceV2CheckInRecord> noCheckInOffDutyList = recordList.stream().filter(
                                (r) -> r.getCheckInType().equals(AttendanceV2CheckInRecord.OffDuty)
                                        && AttendanceV2Helper.isRecordNotSign(r))
                        .collect(Collectors.toList());
                v2Detail.setOffDutyAbsenceTimes(noCheckInOffDutyList.size());
            }
            // 外勤次数
            v2Detail.setFieldWorkTimes(fieldWorkList.size());
            v2Detail.setRecordIdList(
                    recordList.stream().map(AttendanceV2CheckInRecord::getId).collect(Collectors.toList()));
            v2Detail.setGroupId(group.getId());
            v2Detail.setGroupName(group.getGroupName());
            if (shift != null) {
                v2Detail.setShiftId(shift.getId());
                String name = formatShiftName(shift);
                v2Detail.setShiftName(name);
            }
            emc.beginTransaction(AttendanceV2Detail.class);
            emc.persist(v2Detail, CheckPersistType.all);
            emc.commit();
            if (logger.isDebugEnabled()) {
                logger.debug("考勤数据处理完成, {}", v2Detail.toString());
            }
            // 如果有异常打卡数据生成对应的数据
            generateAppealInfo(emc, business, config, recordList, BooleanUtils.isTrue(group.getFieldWorkMarkError()),
                    isWorkDay);
        }

    }

    // 格式化班次名称
    private String formatShiftName(AttendanceV2Shift shift) {
        String name = shift.getShiftName();
        if (shift.getProperties() != null && shift.getProperties().getTimeList() != null
                && !shift.getProperties().getTimeList().isEmpty()) {
            name += " " + shift.getProperties().getTimeList().get(0).getOnDutyTime() + " - "
                    + shift.getProperties().getTimeList().get(shift.getProperties().getTimeList().size() - 1)
                    .getOffDutyTime();
        }
        return name;
    }

    /**
     * 如果开启了是否开启补卡申请功能 生成对应的异常打卡申请数据
     *
     * @param config             配置
     * @param recordList
     * @param fieldWorkMarkError 外勤是否标记为异常数据
     * @param isWorkDay          是否工作日
     * @throws Exception
     */
    private void generateAppealInfo(EntityManagerContainer emc, Business business, AttendanceV2Config config,
                                    List<AttendanceV2CheckInRecord> recordList, boolean fieldWorkMarkError, boolean isWorkDay)
            throws Exception {
        if (emc != null && business != null
                && config != null && BooleanUtils.isTrue(config.getAppealEnable())
                && recordList != null && !recordList.isEmpty()) {
            for (AttendanceV2CheckInRecord record : recordList) {
                List<AttendanceV2AppealInfo> appealList = business.getAttendanceV2ManagerFactory()
                        .listAppealInfoWithRecordId(record.getId());
                // 异常打卡 新增AttendanceV2AppealInfo记录
                if (isWorkDay && record.checkResultException(fieldWorkMarkError)) {
                    if (appealList != null && !appealList.isEmpty()) {
                        logger.info("当前打卡记录已经有申诉数据存在，不需要重复生成！{}", record.getId());
                        continue;
                    }
                    AttendanceV2AppealInfo appealInfo = new AttendanceV2AppealInfo();
                    appealInfo.setRecordId(record.getId());
                    appealInfo.setRecordDateString(record.getRecordDateString());
                    appealInfo.setRecordDate(record.getRecordDate());
                    appealInfo.setUserId(record.getUserId());
                    emc.beginTransaction(AttendanceV2AppealInfo.class);
                    emc.persist(appealInfo, CheckPersistType.all);
                    emc.commit();
                    if (logger.isDebugEnabled()) {
                        logger.debug("生成对应的异常打卡申请数据, {}", appealInfo.toString());
                    }
                } else {
                    // 申诉过的数据不用删除
                    if (StringUtils.isNotEmpty(record.getAppealId())) {
                        continue;
                    }
                    // 正常的打卡记录 需要删除原来有的异常AttendanceV2AppealInfo数据，有可能打卡记录修改过，现在已经是正常的了
                    if (appealList != null && !appealList.isEmpty()) {
                        List<String> deleteIds = new ArrayList<>();
                        for (AttendanceV2AppealInfo appealInfo : appealList) {
                            deleteIds.add(appealInfo.getId());
                        }
                        emc.beginTransaction(AttendanceV2AppealInfo.class);
                        emc.delete(AttendanceV2AppealInfo.class, deleteIds);
                        emc.commit();
                    }
                }
            }
        }
    }

    // 检查当前打卡记录是否在请假时间段内
    private void checkUpdateRecordIsLeave(EntityManagerContainer emc, Business business,
                                          AttendanceV2CheckInRecord record) throws Exception {
        List<AttendanceV2LeaveData> list = business.getAttendanceV2ManagerFactory()
                .listLeaveDataWithRecordTime(record.getUserId(), record.getRecordDate());
        if (list != null && !list.isEmpty()) {
            record.setLeaveDataId(list.get(0).getId());
            emc.beginTransaction(AttendanceV2CheckInRecord.class);
            emc.persist(record, CheckPersistType.all);
            emc.commit();
        }
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

    // 未打卡保存 todo offDutyNext
    private AttendanceV2CheckInRecord saveNoCheckInRecord(EntityManagerContainer emc, String person, String dutyType,
                                                          AttendanceV2Group group, AttendanceV2Shift shift, String cDate,
                                                          String dutyTime, String dutyTimeBeforeLimit, String dutyTimeAfterLimit, boolean offDutyNextDay)
            throws Exception {
        String result = AttendanceV2CheckInRecord.CHECKIN_RESULT_NotSigned;
        AttendanceV2CheckInRecord noCheckRecord = new AttendanceV2CheckInRecord();
        noCheckRecord.setCheckInType(dutyType);
        noCheckRecord.setCheckInResult(result);
        noCheckRecord.setUserId(person);
        // 打卡时间
        if (StringUtils.isEmpty(dutyTime)) {
            if (AttendanceV2CheckInRecord.OnDuty.equals(dutyType)) {
                dutyTime = "09:00";
            } else {
                dutyTime = "18:00";
            }
        }
        Date onDutyTime = DateTools.parse(cDate + " " + dutyTime, DateTools.format_yyyyMMddHHmm);
        if (AttendanceV2CheckInRecord.OffDuty.equals(dutyType) && offDutyNextDay) {
            Date nextDate = DateTools.addDay(onDutyTime, 1);
            Date now = new Date();
            if (now.before(nextDate)) { // 跨天的数据 有可能还未到打卡时间
                result = AttendanceV2CheckInRecord.CHECKIN_RESULT_PreCheckIn;
                noCheckRecord.setCheckInResult(result);
            }
            noCheckRecord.setRecordDate(nextDate);
        } else {
            noCheckRecord.setRecordDate(onDutyTime);
        }
        noCheckRecord.setRecordDateString(cDate);
        noCheckRecord.setPreDutyTime(dutyTime);
        noCheckRecord.setPreDutyTimeBeforeLimit(dutyTimeBeforeLimit);
        noCheckRecord.setPreDutyTimeAfterLimit(dutyTimeAfterLimit);
        noCheckRecord.setSourceType(AttendanceV2CheckInRecord.SOURCE_TYPE_AUTO_CHECK);
        noCheckRecord.setSourceDevice("其他");
        noCheckRecord.setDescription("系统生成，未打卡记录");
        noCheckRecord.setGroupId(group.getId());
        noCheckRecord.setGroupName(group.getGroupName());
        noCheckRecord.setGroupCheckType(group.getCheckType());
        if (shift != null) {
            noCheckRecord.setShiftId(shift.getId());
            noCheckRecord.setShiftName(shift.getShiftName());
        }
        emc.beginTransaction(AttendanceV2CheckInRecord.class);
        emc.persist(noCheckRecord, CheckPersistType.all);
        emc.commit();
        logger.info("打卡记录保存：{}, {}, {} ", person, cDate, result);
        return noCheckRecord;
    }
}
