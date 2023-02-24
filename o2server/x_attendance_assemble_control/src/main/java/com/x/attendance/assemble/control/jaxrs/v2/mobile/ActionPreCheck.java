package com.x.attendance.assemble.control.jaxrs.v2.mobile;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
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
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
            List<AttendanceV2Group> groups = business.getAttendanceV2ManagerFactory().listGroupWithPerson(effectivePerson.getDistinguishedName());
            if (groups == null || groups.isEmpty()) {
                result.setData(cannotCheckIn("没有对应的考勤组"));
                return result;
            }
            AttendanceV2Group group = groups.get(0);
            // 查询班次对象
            AttendanceV2Shift shift = emc.find(group.getShiftId(), AttendanceV2Shift.class);
            if (shift == null) {
                result.setData(cannotCheckIn("没有对应的班次信息"));
                return result;
            }
            List<AttendanceV2ShiftCheckTime> timeList = shift.getProperties().getTimeList();
            if (timeList == null || timeList.isEmpty()) {
                result.setData( cannotCheckIn("没有对应的上下班打卡时间"));
                return result;
            }
            // 查询打卡记录
            Date nowDate = new Date();
            String today = DateTools.format(nowDate, DateTools.format_yyyyMMdd);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("日期：{}", today);
            }
            // 按照时间顺序查询出打卡列表
            List<AttendanceV2CheckInRecord> recordList = business.getAttendanceV2ManagerFactory().listRecordWithPersonAndDate(effectivePerson.getDistinguishedName(), today);
            // 如果没有数据，先根据班次打卡信息 预存打卡数据
            if (recordList == null || recordList.isEmpty()) {
                recordList = new ArrayList<>();
                for (AttendanceV2ShiftCheckTime shiftCheckTime : timeList) {
                    // 上班打卡
                    AttendanceV2CheckInRecord onDutyRecord =  savePreCheckInRecord(emc, effectivePerson.getDistinguishedName(), AttendanceV2CheckInRecord.OnDuty, group, shift, today,
                            shiftCheckTime.getOnDutyTime(), shiftCheckTime.getOnDutyTimeBeforeLimit(), shiftCheckTime.getOnDutyTimeAfterLimit());
                    recordList.add(onDutyRecord);
                    // 下班打卡
                    AttendanceV2CheckInRecord offDutyRecord =  savePreCheckInRecord(emc, effectivePerson.getDistinguishedName(), AttendanceV2CheckInRecord.OffDuty, group, shift, today,
                            shiftCheckTime.getOffDutyTime(), shiftCheckTime.getOffDutyTimeBeforeLimit(), shiftCheckTime.getOffDutyTimeAfterLimit());
                    recordList.add(offDutyRecord);
                }
            }

            for (int i = 0; i < recordList.size(); i++) {
                AttendanceV2CheckInRecord record = recordList.get(i);
                // 不是预打卡数据 跳过
                if (record.getCheckInResult().equals(AttendanceV2CheckInRecord.CHECKIN_RESULT_PreCheckIn)) {
                    if (StringUtils.isNotEmpty(record.getPreDutyTimeAfterLimit())) { // 有打卡结束限制
                        Date onDutyAfterTime =  DateTools.parse(today + " " + record.getPreDutyTimeAfterLimit(), DateTools.format_yyyyMMddHHmm);
                        if (nowDate.after(onDutyAfterTime)) { // 超过了打卡结束限制时间，直接生成未打卡数据
                            update2NoCheckInRecord(emc, record);
                        }
                    } else {
                        Date onDutyTime = DateTools.parse(today + " " + record.getPreDutyTime(), DateTools.format_yyyyMMddHHmm);
                        if (nowDate.after(onDutyTime)) {
                            // 查询下一条数据 下班打卡
                            if (i < recordList.size()-1) {
                                AttendanceV2CheckInRecord nextRecord = recordList.get( i + 1 );
                                Date offDutyTime = DateTools.parse(today + " " + nextRecord.getPreDutyTime(), DateTools.format_yyyyMMddHHmm);
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

//
//            List<WoCheckInAndRecordItem> recordItemList = new ArrayList<>();
//            int index = 0;
//            for (AttendanceV2ShiftCheckTime attendanceV2ShiftCheckTime : timeList) {
//                // 上班打卡
//                WoCheckInAndRecordItem onDutyItem = new WoCheckInAndRecordItem();
//                onDutyItem.setCheckInType(AttendanceV2CheckInRecord.OnDuty);
//                onDutyItem.setDutyTime(attendanceV2ShiftCheckTime.getOnDutyTime());
//                onDutyItem.setDutyTimeBeforeLimit(attendanceV2ShiftCheckTime.getOnDutyTimeBeforeLimit());
//                onDutyItem.setDutyTimeAfterLimit(attendanceV2ShiftCheckTime.getOnDutyTimeAfterLimit());
//                AttendanceV2CheckInRecord onDutyRecord = hasCheckedRecord(recordList, index, AttendanceV2CheckInRecord.OnDuty);
//                if (onDutyRecord != null) {
//                    onDutyItem.setCheckInRecordId(onDutyRecord.getId());
//                    onDutyItem.setCheckInResult(onDutyRecord.getCheckInResult());
//                    onDutyItem.setRecordDate(onDutyRecord.getRecordDate());
//                } else {
//                    if (StringUtils.isNotEmpty(attendanceV2ShiftCheckTime.getOnDutyTimeAfterLimit())) { // 有结束限制
//                        Date onDutyAfterTime =  DateTools.parse(today+" "+attendanceV2ShiftCheckTime.getOnDutyTimeAfterLimit(), DateTools.format_yyyyMMddHHmm);
//                        if (nowDate.after(onDutyAfterTime)) { // 超过结束时间 生成未打卡数据
//                            AttendanceV2CheckInRecord noCheckRecord = saveNoCheckInRecord(emc, effectivePerson.getDistinguishedName(), AttendanceV2CheckInRecord.OnDuty, group, shift, nowDate, today);
//                            onDutyItem.setCheckInRecordId(noCheckRecord.getId());
//                            onDutyItem.setCheckInResult(noCheckRecord.getCheckInResult());
//                            onDutyItem.setRecordDate(noCheckRecord.getRecordDate());
//                        }
//                    } else {
//                        Date onDutyTime = DateTools.parse(today+" "+attendanceV2ShiftCheckTime.getOnDutyTime(), DateTools.format_yyyyMMddHHmm);
//                        if (nowDate.after(onDutyTime)) {
//                            Date offDutyTime = DateTools.parse(today + " " + attendanceV2ShiftCheckTime.getOffDutyTime(), DateTools.format_yyyyMMddHHmm);
//                            long minutes = (offDutyTime.getTime() - onDutyTime.getTime()) / 60000 / 2; // 一半间隔时间
//                            Date middleTime = DateTools.addMinutes(onDutyTime, (int) minutes);
//                            if (nowDate.after(middleTime)) { // 生成未打卡数据
//                                AttendanceV2CheckInRecord noCheckRecord = saveNoCheckInRecord(emc, effectivePerson.getDistinguishedName(), AttendanceV2CheckInRecord.OnDuty, group, shift, nowDate, today);
//                                onDutyItem.setCheckInRecordId(noCheckRecord.getId());
//                                onDutyItem.setCheckInResult(noCheckRecord.getCheckInResult());
//                                onDutyItem.setRecordDate(noCheckRecord.getRecordDate());
//                            }
//                        }
//                    }
//                }
//                recordItemList.add(onDutyItem);
//                // 下班打卡
//                WoCheckInAndRecordItem offDutyItem = new WoCheckInAndRecordItem();
//                offDutyItem.setCheckInType(AttendanceV2CheckInRecord.OffDuty);
//                offDutyItem.setDutyTime(attendanceV2ShiftCheckTime.getOffDutyTime());
//                offDutyItem.setDutyTimeBeforeLimit(attendanceV2ShiftCheckTime.getOffDutyTimeBeforeLimit());
//                offDutyItem.setDutyTimeAfterLimit(attendanceV2ShiftCheckTime.getOffDutyTimeAfterLimit());
//                AttendanceV2CheckInRecord offDutyRecord = hasCheckedRecord(recordList, index, AttendanceV2CheckInRecord.OffDuty);
//                if (offDutyRecord != null) {
//                    offDutyItem.setCheckInRecordId(offDutyRecord.getId());
//                    offDutyItem.setCheckInResult(offDutyRecord.getCheckInResult());
//                    offDutyItem.setRecordDate(offDutyRecord.getRecordDate());
//                } else {
//                    if (StringUtils.isNotEmpty(attendanceV2ShiftCheckTime.getOffDutyTimeAfterLimit())) {
//                        Date offDutyAfterTime =  DateTools.parse(today+" "+attendanceV2ShiftCheckTime.getOffDutyTimeAfterLimit(), DateTools.format_yyyyMMddHHmm);
//                        if (nowDate.after(offDutyAfterTime)) { // 超过结束时间 生成未打卡数据
//                            AttendanceV2CheckInRecord noCheckRecord = saveNoCheckInRecord(emc, effectivePerson.getDistinguishedName(), AttendanceV2CheckInRecord.OffDuty, group, shift, nowDate, today);
//                            offDutyItem.setCheckInRecordId(noCheckRecord.getId());
//                            offDutyItem.setCheckInResult(noCheckRecord.getCheckInResult());
//                            offDutyItem.setRecordDate(noCheckRecord.getRecordDate());
//                        }
//                    } else {
//                        Date offDutyTime = DateTools.parse(today + " " + attendanceV2ShiftCheckTime.getOffDutyTime(), DateTools.format_yyyyMMddHHmm);
//                        if ((timeList.size() - 1) > index) {
//                            AttendanceV2ShiftCheckTime nextCheckTime = timeList.get(index+1);
//                            Date nextOnDutyTime = DateTools.parse(today + " " + nextCheckTime.getOnDutyTime(), DateTools.format_yyyyMMddHHmm);
//                            long minutes = (nextOnDutyTime.getTime() - offDutyTime.getTime()) / 60000 / 2; // 一半间隔时间
//                            Date middleTime = DateTools.addMinutes(offDutyTime, (int) minutes);
//                            if (nowDate.after(middleTime)) { // 生成未打卡数据
//                                AttendanceV2CheckInRecord noCheckRecord = saveNoCheckInRecord(emc, effectivePerson.getDistinguishedName(), AttendanceV2CheckInRecord.OffDuty, group, shift, nowDate, today);
//                                onDutyItem.setCheckInRecordId(noCheckRecord.getId());
//                                onDutyItem.setCheckInResult(noCheckRecord.getCheckInResult());
//                                onDutyItem.setRecordDate(noCheckRecord.getRecordDate());
//                            }
//                        }
//                    }
//                }
//                recordItemList.add(offDutyItem);
//
//                index++;
//            }

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



    // 预存打卡数据保存
    private AttendanceV2CheckInRecord savePreCheckInRecord(EntityManagerContainer emc, String person,String dutyType,
                                                           AttendanceV2Group group, AttendanceV2Shift shift, String today,
                                                           String dutyTime, String dutyTimeBeforeLimit, String dutyTimeAfterLimit) throws Exception {
        AttendanceV2CheckInRecord noCheckRecord = new AttendanceV2CheckInRecord();
        noCheckRecord.setCheckInType(dutyType);
        noCheckRecord.setCheckInResult(AttendanceV2CheckInRecord.CHECKIN_RESULT_PreCheckIn);
        noCheckRecord.setUserId(person);
        // 打卡时间
        Date onDutyTime = DateTools.parse(today+" "+dutyTime, DateTools.format_yyyyMMddHHmm);
        noCheckRecord.setRecordDate(onDutyTime);
        noCheckRecord.setRecordDateString(today);
        noCheckRecord.setPreDutyTime(dutyTime);
        noCheckRecord.setPreDutyTimeBeforeLimit(dutyTimeBeforeLimit);
        noCheckRecord.setPreDutyTimeAfterLimit(dutyTimeAfterLimit);
        noCheckRecord.setSourceType(AttendanceV2CheckInRecord.SOURCE_TYPE_AUTO_CHECK);
        noCheckRecord.setSourceDevice("其他");
        noCheckRecord.setDescription("系统生成，预打卡记录");
        noCheckRecord.setGroupId(group.getId());
        noCheckRecord.setGroupName(group.getGroupName());
        noCheckRecord.setShiftId(shift.getId());
        noCheckRecord.setShiftName(shift.getShiftName());
        emc.beginTransaction(AttendanceV2CheckInRecord.class);
        emc.persist(noCheckRecord, CheckPersistType.all);
        emc.commit();
        return noCheckRecord;
    }

    private void update2NoCheckInRecord(EntityManagerContainer emc, AttendanceV2CheckInRecord record) throws Exception {
        emc.beginTransaction(AttendanceV2CheckInRecord.class);
        record.setCheckInResult(AttendanceV2CheckInRecord.CHECKIN_RESULT_NotSigned);
        record.setSourceType(AttendanceV2CheckInRecord.SOURCE_TYPE_AUTO_CHECK);
        record.setSourceDevice("其他");
        record.setDescription("系统生成，未打卡记录");
        emc.persist(record, CheckPersistType.all);
        emc.commit();
    }

    // 未打卡数据保存
    private AttendanceV2CheckInRecord saveNoCheckInRecord(EntityManagerContainer emc, String person,String dutyType, AttendanceV2Group group, AttendanceV2Shift shift, Date nowDate, String today) throws Exception {
        AttendanceV2CheckInRecord noCheckRecord = new AttendanceV2CheckInRecord();
        noCheckRecord.setCheckInType(dutyType);
        noCheckRecord.setCheckInResult(AttendanceV2CheckInRecord.CHECKIN_RESULT_NotSigned);
        noCheckRecord.setUserId(person);
        noCheckRecord.setRecordDate(nowDate);
        noCheckRecord.setRecordDateString(today);
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



    private Wo cannotCheckIn(String reason) {
        Wo wo = new Wo();
        wo.setCanCheckIn(false);
        wo.setCannotCheckInReason(reason);
        return wo;
    }


    public static class Wo extends GsonPropertyObject {

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
