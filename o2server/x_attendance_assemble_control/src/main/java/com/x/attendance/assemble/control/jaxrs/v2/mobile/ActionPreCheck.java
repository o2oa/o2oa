package com.x.attendance.assemble.control.jaxrs.v2.mobile;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.ThisApplication;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.WoGroupShift;
import com.x.attendance.entity.v2.*;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * 当前是查询打卡数据的接口
 * 在打开之前必须先查询这个接口，根据这个接口返回的打卡结果列表数据进行打卡
 * 打卡结果checkInResult 中，PreCheckIn是预存数据需要打卡，其它是已经打卡的结果
 * Created by fancyLou on 2023/2/21.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionPreCheck extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionPreCheck.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
        ActionResult<Wo> result = new ActionResult<>();
        if (effectivePerson == null || StringUtils.isEmpty(effectivePerson.getDistinguishedName())) {
            throw new ExceptionEmptyParameter("当前用户信息");
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            // 查询当前用户的考勤组
            Business business = new Business(emc);
            Date nowDate = new Date();
            String today = DateTools.format(nowDate, DateTools.format_yyyyMMdd);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("日期：{}", today);
            }
            WoGroupShift woGroupShift = business.getAttendanceV2ManagerFactory().getGroupShiftByPersonDate(effectivePerson.getDistinguishedName(), today);
            if (woGroupShift == null || woGroupShift.getGroup() == null) {
                result.setData(cannotCheckIn("没有对应的考勤组"));
                return result;
            }
            AttendanceV2Group group = woGroupShift.getGroup();
            // 处理并发的问题
            List<AttendanceV2CheckInRecord> recordList = ThisApplication.executor
                    .submit(new CallableImpl(effectivePerson.getDistinguishedName(), group, woGroupShift.getShift())).get();
            if (recordList == null || recordList.isEmpty()) {
                result.setData(cannotCheckIn("没有对应的上下班打卡时间"));
                return result;
            }
            Wo wo = new Wo();
            wo.setCanCheckIn(true);
            wo.setAllowFieldWork(group.getAllowFieldWork());
            wo.setRequiredFieldWorkRemarks(group.getRequiredFieldWorkRemarks());
            wo.setCheckItemList(recordList);
            if (group.getWorkPlaceIdList() != null && !group.getWorkPlaceIdList().isEmpty()) {
                List<AttendanceV2WorkPlace> workPlaceList = new ArrayList<>();
                for (String id : group.getWorkPlaceIdList()) {
                    AttendanceV2WorkPlace workPlace = emc.find(id, AttendanceV2WorkPlace.class);
                    workPlaceList.add(workPlace);
                }
                wo.setWorkPlaceList(workPlaceList);
            }
            result.setData(wo);
        }
        return result;
    }

    private class CallableImpl implements Callable<List<AttendanceV2CheckInRecord>> {

        private String person;
        private AttendanceV2Group group;
        private AttendanceV2Shift shift;

        private CallableImpl(String person, AttendanceV2Group group, AttendanceV2Shift shift) {
            this.person = person;
            this.group = group;
            this.shift = shift;
        }

        @Override
        public List<AttendanceV2CheckInRecord> call() throws Exception {
            // 查询打卡记录
            Date nowDate = new Date();
            String today = DateTools.format(nowDate, DateTools.format_yyyyMMdd);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("日期：{}", today);
            }
            try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
                // 查询当前用户的考勤组
                Business business = new Business(emc);
                // 按照时间顺序查询出打卡列表
                List<AttendanceV2CheckInRecord> recordList = business.getAttendanceV2ManagerFactory()
                        .listRecordWithPersonAndDate(person, today);
                if (group.getCheckType().equals(AttendanceV2Group.CHECKTYPE_Arrangement)) {
                    // 排班制数据处理 排班制有可能有跨天的打卡 所以要先查询昨天的打卡记录
                    Date yesterday = DateTools.addDay(nowDate, -1);
                    String yesterdayString = DateTools.format(yesterday, DateTools.format_yyyyMMdd);
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("昨天日期：{}", yesterdayString);
                    }
                    List<AttendanceV2CheckInRecord> yesterdayRecordList = business.getAttendanceV2ManagerFactory()
                        .listRecordWithPersonAndDate(person, yesterdayString);
                    if (yesterdayRecordList == null || yesterdayRecordList.isEmpty()) {
                        if (shift != null) {
                            yesterdayRecordList = dealShiftForRecord(shift, emc, business, person, group, yesterday, yesterdayString, null);
                        }
                    }
                    if (yesterdayRecordList != null && !yesterdayRecordList.isEmpty()) {
                        AttendanceV2CheckInRecord last = yesterdayRecordList.get(yesterdayRecordList.size()-1);
                        // 昨天的数据 并且跨天
                        if (last.getOffDutyNextDay()) {
                            Date onDutyAfterTime;
                            if (StringUtils.isEmpty(last.getPreDutyTimeAfterLimit())) {
                                onDutyAfterTime = DateTools.parse(today + " " + last.getPreDutyTime(), DateTools.format_yyyyMMddHHmm);
                                onDutyAfterTime = DateTools.addMinutes(onDutyAfterTime, 60);// 跨天的最后一条数据 超过 60 分钟
                            } else {
                                onDutyAfterTime = DateTools.parse(today + " " + last.getPreDutyTimeAfterLimit(), DateTools.format_yyyyMMddHHmm);
                            }
                            // 如果没有在限制时间结束前 就是返回昨天的打卡记录
                            if (!nowDate.after(onDutyAfterTime)) {
                                LOGGER.info("返回昨日的数据，有跨天的还未完成的打卡");
                                return yesterdayRecordList;
                            }
                        }
                    }
                }
                if (recordList != null && !recordList.isEmpty()) {
                    // 自动处理 已经过来的打卡记录 记录为未打卡
                    dealWithOvertimeRecord(emc, nowDate, today, recordList);
                    return recordList;
                }

                if (shift != null) {
                    recordList = dealShiftForRecord(shift, emc, business, person, group, nowDate, today, recordList);
                }
 
                // 如果没有数据，可能是自由工时 或者 休息日没有班次信息的情况下 只需要生成一条上班一条下班的打卡记录
                if (recordList == null || recordList.isEmpty()) {
                    recordList = new ArrayList<>();
                    // 上班打卡
                    AttendanceV2CheckInRecord onDutyRecord = savePreCheckInRecord(emc, person,
                            AttendanceV2CheckInRecord.OnDuty, group, null, today,
                            null, null, null, false);
                    recordList.add(onDutyRecord);
                    // 下班打卡
                    AttendanceV2CheckInRecord offDutyRecord = savePreCheckInRecord(emc, person,
                            AttendanceV2CheckInRecord.OffDuty, group, null, today,
                            null, null, null, false);
                    recordList.add(offDutyRecord);
                }

                return recordList;
            }
        }

    }

    /**
     * 根据班次处理预存打卡记录
     * 
     * @param shift
     * @param emc
     * @param business
     * @param person
     * @param group
     * @param nowDate
     * @param today
     * @param recordList
     * @throws Exception
     */
    private List<AttendanceV2CheckInRecord> dealShiftForRecord(AttendanceV2Shift shift, EntityManagerContainer emc, Business business,
            String person, AttendanceV2Group group, Date nowDate, String today,
            List<AttendanceV2CheckInRecord> recordList) throws Exception {
        List<AttendanceV2ShiftCheckTime> timeList = shift.getProperties().getTimeList();
        if (timeList == null || timeList.isEmpty()) {
            LOGGER.info("没有对应的上下班打卡时间");
            // return null;
            throw new ExceptionEmptyParameter("没有对应的上下班打卡时间");
        }
        // 如果没有数据，先根据班次打卡信息 预存打卡数据
        if (recordList == null || recordList.isEmpty()) {
            recordList = new ArrayList<>();
            for (AttendanceV2ShiftCheckTime shiftCheckTime : timeList) {
                // 上班打卡
                AttendanceV2CheckInRecord onDutyRecord = savePreCheckInRecord(emc, person,
                        AttendanceV2CheckInRecord.OnDuty, group, shift, today,
                        shiftCheckTime.getOnDutyTime(), shiftCheckTime.getOnDutyTimeBeforeLimit(),
                        shiftCheckTime.getOnDutyTimeAfterLimit(), false);
                recordList.add(onDutyRecord);
                // 下班打卡
                
                AttendanceV2CheckInRecord offDutyRecord = savePreCheckInRecord(emc, person,
                        AttendanceV2CheckInRecord.OffDuty, group, shift, today,
                        shiftCheckTime.getOffDutyTime(), shiftCheckTime.getOffDutyTimeBeforeLimit(),
                        shiftCheckTime.getOffDutyTimeAfterLimit(), BooleanUtils.isTrue(shiftCheckTime.getOffDutyNextDay()));
                recordList.add(offDutyRecord);
            }
        }
        // 自动处理 已经过来的打卡记录 记录为未打卡
        dealWithOvertimeRecord(emc, nowDate, today, recordList);
        return recordList;
    }

    /**
     * 处理超过时间的打卡记录 标记为未打卡
     * 
     * @param emc
     * @param nowDate
     * @param today
     * @param recordList
     * @throws Exception
     */
    private void dealWithOvertimeRecord(EntityManagerContainer emc, Date nowDate, String today,
            List<AttendanceV2CheckInRecord> recordList) throws Exception {
        for (int i = 0; i < recordList.size(); i++) {
            AttendanceV2CheckInRecord record = recordList.get(i);
            // 不是预打卡数据 跳过
            if (record.getCheckInResult().equals(AttendanceV2CheckInRecord.CHECKIN_RESULT_PreCheckIn)) {
                if (StringUtils.isNotEmpty(record.getPreDutyTimeAfterLimit())) { // 有打卡结束限制
                    Date onDutyAfterTime = DateTools.parse(today + " " + record.getPreDutyTimeAfterLimit(),
                            DateTools.format_yyyyMMddHHmm);
                    if (nowDate.after(onDutyAfterTime)) { // 超过了打卡结束限制时间，直接生成未打卡数据
                        update2NoCheckInRecord(emc, record);
                    }
                } else {
                    Date onDutyTime = DateTools.parse(today + " " + record.getPreDutyTime(),
                            DateTools.format_yyyyMMddHHmm);
                    if (nowDate.after(onDutyTime)) {
                        // 查询下一条数据 下班打卡
                        if (i < recordList.size() - 1) {
                            AttendanceV2CheckInRecord nextRecord = recordList.get(i + 1);
                            Date offDutyTime = DateTools.parse(today + " " + nextRecord.getPreDutyTime(),
                                    DateTools.format_yyyyMMddHHmm);
                            if (nextRecord.getOffDutyNextDay()) { // 跨天的数据
                                offDutyTime = DateTools.addDay(offDutyTime, 1);
                            }
                            long minutes = (offDutyTime.getTime() - onDutyTime.getTime()) / 60000 / 2; // 一半间隔时间
                            Date middleTime = DateTools.addMinutes(onDutyTime, (int) minutes);
                            if (nowDate.after(middleTime)) { // 生成未打卡数据
                                update2NoCheckInRecord(emc, record);
                            }
                        }

                    }
                }
            }
        }
    }

    /**
     * 预存打卡数据保存
     * @offDutyNextDay 是否是次日 最后一条下班打卡才有
     */
    private AttendanceV2CheckInRecord savePreCheckInRecord(EntityManagerContainer emc, String person, String dutyType,
            AttendanceV2Group group, AttendanceV2Shift shift, String today,
            String dutyTime, String dutyTimeBeforeLimit, String dutyTimeAfterLimit, boolean offDutyNextDay) throws Exception {
        AttendanceV2CheckInRecord noCheckRecord = new AttendanceV2CheckInRecord();
        noCheckRecord.setCheckInType(dutyType);
        noCheckRecord.setCheckInResult(AttendanceV2CheckInRecord.CHECKIN_RESULT_PreCheckIn);
        noCheckRecord.setUserId(person);
        // 打卡时间
        if (StringUtils.isEmpty(dutyTime)) {
            if (AttendanceV2CheckInRecord.OnDuty.equals(dutyType)) {
                dutyTime = "09:00";
            } else {
                dutyTime = "18:00";
            }
        }
        Date onDutyTime = DateTools.parse(today + " " + dutyTime, DateTools.format_yyyyMMddHHmm);
        if (AttendanceV2CheckInRecord.OffDuty.equals(dutyType) && offDutyNextDay) {
            Date nextDate = DateTools.addDay(onDutyTime, 1);
            noCheckRecord.setRecordDate(nextDate);
        } else {
            noCheckRecord.setRecordDate(onDutyTime);
        }
        noCheckRecord.setRecordDateString(today);
        noCheckRecord.setPreDutyTime(dutyTime);
        noCheckRecord.setPreDutyTimeBeforeLimit(dutyTimeBeforeLimit);
        noCheckRecord.setPreDutyTimeAfterLimit(dutyTimeAfterLimit);
        noCheckRecord.setSourceType(AttendanceV2CheckInRecord.SOURCE_TYPE_AUTO_CHECK);
        noCheckRecord.setSourceDevice("其他");
        noCheckRecord.setDescription("系统生成，预打卡记录");
        noCheckRecord.setGroupId(group.getId());
        noCheckRecord.setGroupName(group.getGroupName());
        noCheckRecord.setGroupCheckType(group.getCheckType());
        noCheckRecord.setOffDutyNextDay(offDutyNextDay);
        if (shift != null) {
            noCheckRecord.setShiftId(shift.getId());
            noCheckRecord.setShiftName(shift.getShiftName());
        }
        emc.beginTransaction(AttendanceV2CheckInRecord.class);
        emc.persist(noCheckRecord, CheckPersistType.all);
        emc.commit();
        LOGGER.info("打卡记录保存：{}, {}, {} ", person, today, AttendanceV2CheckInRecord.CHECKIN_RESULT_PreCheckIn);
        return noCheckRecord;
    }

    /**
     * 更新为 未打卡
     * 
     * @param emc
     * @param record
     * @throws Exception
     */
    private void update2NoCheckInRecord(EntityManagerContainer emc, AttendanceV2CheckInRecord record) throws Exception {
        emc.beginTransaction(AttendanceV2CheckInRecord.class);
        record.setCheckInResult(AttendanceV2CheckInRecord.CHECKIN_RESULT_NotSigned);
        record.setSourceType(AttendanceV2CheckInRecord.SOURCE_TYPE_AUTO_CHECK);
        record.setSourceDevice("其他");
        record.setDescription("系统生成，缺卡记录");
        emc.persist(record, CheckPersistType.all);
        emc.commit();
    }

    private Wo cannotCheckIn(String reason) {
        Wo wo = new Wo();
        wo.setCanCheckIn(false);
        wo.setCannotCheckInReason(reason);
        return wo;
    }

    public static class Wo extends GsonPropertyObject {

        private static final long serialVersionUID = -2916750848315546343L;

        @FieldDescribe("是否")
        private Boolean free;

        @FieldDescribe("是否允许外勤打卡")
        private Boolean allowFieldWork;
        @FieldDescribe("外勤打卡备注是否必填")
        private Boolean requiredFieldWorkRemarks;
        @FieldDescribe("当前时间是否能够打卡")
        private Boolean canCheckIn;
        @FieldDescribe("不能打卡的原因")
        private String cannotCheckInReason;
        @FieldDescribe("打卡列表")
        private List<AttendanceV2CheckInRecord> checkItemList;
        @FieldDescribe("工作场所列表")
        private List<AttendanceV2WorkPlace> workPlaceList;

        public Boolean getAllowFieldWork() {
            return allowFieldWork;
        }

        public void setAllowFieldWork(Boolean allowFieldWork) {
            this.allowFieldWork = allowFieldWork;
        }

        public Boolean getRequiredFieldWorkRemarks() {
            return requiredFieldWorkRemarks;
        }

        public void setRequiredFieldWorkRemarks(Boolean requiredFieldWorkRemarks) {
            this.requiredFieldWorkRemarks = requiredFieldWorkRemarks;
        }

        public Boolean getCanCheckIn() {
            return canCheckIn;
        }

        public void setCanCheckIn(Boolean canCheckIn) {
            this.canCheckIn = canCheckIn;
        }

        public String getCannotCheckInReason() {
            return cannotCheckInReason;
        }

        public void setCannotCheckInReason(String cannotCheckInReason) {
            this.cannotCheckInReason = cannotCheckInReason;
        }

        public List<AttendanceV2CheckInRecord> getCheckItemList() {
            return checkItemList;
        }

        public void setCheckItemList(List<AttendanceV2CheckInRecord> checkItemList) {
            this.checkItemList = checkItemList;
        }

        public List<AttendanceV2WorkPlace> getWorkPlaceList() {
            return workPlaceList;
        }

        public void setWorkPlaceList(List<AttendanceV2WorkPlace> workPlaceList) {
            this.workPlaceList = workPlaceList;
        }
    }

    public static class WoCheckInAndRecordItem extends GsonPropertyObject {
        private static final long serialVersionUID = 9162618804645395372L;
        @FieldDescribe("考勤类型, OnDuty：上班 OffDuty：下班")
        private String checkInType;
        @FieldDescribe("打卡时间")
        private String dutyTime;
        @FieldDescribe("打卡前限制")
        private String dutyTimeBeforeLimit;
        @FieldDescribe("打卡后限制")
        private String dutyTimeAfterLimit;

        /// 已打卡字段
        @FieldDescribe("打卡结果id")
        private String checkInRecordId;
        @FieldDescribe("打卡结果")
        private String checkInResult;
        @FieldDescribe("打卡记录日期")
        private Date recordDate;

        public String getCheckInType() {
            return checkInType;
        }

        public void setCheckInType(String checkInType) {
            this.checkInType = checkInType;
        }

        public String getDutyTime() {
            return dutyTime;
        }

        public void setDutyTime(String dutyTime) {
            this.dutyTime = dutyTime;
        }

        public String getDutyTimeBeforeLimit() {
            return dutyTimeBeforeLimit;
        }

        public void setDutyTimeBeforeLimit(String dutyTimeBeforeLimit) {
            this.dutyTimeBeforeLimit = dutyTimeBeforeLimit;
        }

        public String getDutyTimeAfterLimit() {
            return dutyTimeAfterLimit;
        }

        public void setDutyTimeAfterLimit(String dutyTimeAfterLimit) {
            this.dutyTimeAfterLimit = dutyTimeAfterLimit;
        }

        public String getCheckInRecordId() {
            return checkInRecordId;
        }

        public void setCheckInRecordId(String checkInRecordId) {
            this.checkInRecordId = checkInRecordId;
        }

        public String getCheckInResult() {
            return checkInResult;
        }

        public void setCheckInResult(String checkInResult) {
            this.checkInResult = checkInResult;
        }

        public Date getRecordDate() {
            return recordDate;
        }

        public void setRecordDate(Date recordDate) {
            this.recordDate = recordDate;
        }
    }
}
