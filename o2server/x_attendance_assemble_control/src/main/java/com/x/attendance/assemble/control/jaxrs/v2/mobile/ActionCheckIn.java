package com.x.attendance.assemble.control.jaxrs.v2.mobile;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionNotExistObject;
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

import java.util.Date;
import java.util.List;

/**
 * Created by fancyLou on 2023/2/22.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionCheckIn extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionCheckIn.class);


    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
        if (effectivePerson == null || StringUtils.isEmpty(effectivePerson.getDistinguishedName())) {
            throw new ExceptionEmptyParameter("当前用户信息");
        }
        ActionResult<Wo> result = new ActionResult<>();
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            if (StringUtils.isEmpty(wi.getCheckInType()) || (!wi.getCheckInType().equals(AttendanceV2CheckInRecord.OnDuty) && !wi.getCheckInType().equals(AttendanceV2CheckInRecord.OffDuty))) {
                throw new ExceptionEmptyParameter("打卡类型");
            }
            if (StringUtils.isEmpty(wi.getLatitude()) || StringUtils.isEmpty(wi.getLongitude())) {
                throw new ExceptionEmptyParameter("经纬度");
            }
            if ((wi.getFieldWork() == null || !wi.getFieldWork()) && StringUtils.isEmpty(wi.getWorkPlaceId())) {
                throw new ExceptionEmptyParameter("打卡工作场所id");
            }
            // 查询当前用户的考勤组
            Business business = new Business(emc);
            List<AttendanceV2Group> groups = business.getAttendanceV2ManagerFactory().listGroupWithPerson(effectivePerson.getDistinguishedName());
            if (groups == null || groups.isEmpty()) {
                throw new ExceptionNotExistObject("考勤组信息");
            }
            AttendanceV2Group group = groups.get(0);
            // 查询班次对象
            AttendanceV2Shift shift = emc.find(group.getShiftId(), AttendanceV2Shift.class);
            if (shift == null || shift.getProperties() == null) {
                throw new ExceptionNotExistObject("班次信息");
            }
            List<AttendanceV2ShiftCheckTime> timeList = shift.getProperties().getTimeList();
            if (timeList == null || timeList.isEmpty()) {
                throw new ExceptionNotExistObject("对应的上下班打卡时间");
            }
            AttendanceV2WorkPlace workPlace = null;
            if (StringUtils.isNotEmpty(wi.getWorkPlaceId())) {
                workPlace = emc.find(wi.getWorkPlaceId(), AttendanceV2WorkPlace.class);
            }
            // 查询打卡记录
            Date nowDate = new Date();
            String today = DateTools.format(nowDate, DateTools.format_yyyyMMdd);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("日期：{}", today);
            }
            // 按照时间顺序查询出打卡列表
            List<AttendanceV2CheckInRecord> recordList = business.getAttendanceV2ManagerFactory().listRecordWithPersonAndDate(effectivePerson.getDistinguishedName(), today);

            int index = 0;
            for (AttendanceV2ShiftCheckTime attendanceV2ShiftCheckTime : timeList) {
                // 上班打卡
                if (wi.getCheckInType().equals(AttendanceV2CheckInRecord.OnDuty)) {
                    AttendanceV2CheckInRecord onDutyRecord = hasCheckedRecord(recordList, index, AttendanceV2CheckInRecord.OnDuty);
                    if (onDutyRecord == null) {
                        if (StringUtils.isNotEmpty(attendanceV2ShiftCheckTime.getOnDutyTimeBeforeLimit())) {
                            Date beforeOnDuty = DateTools.parse(today + " " + attendanceV2ShiftCheckTime.getOnDutyTimeBeforeLimit(), DateTools.format_yyyyMMddHHmm);
                            if (nowDate.before(beforeOnDuty)) { // 不到开始时间不能打卡
                                if (LOGGER.isDebugEnabled()) {
                                    LOGGER.debug("不到开始时间不能打卡!!!!");
                                }
                                break;
                            }
                        }
                        if (StringUtils.isNotEmpty(attendanceV2ShiftCheckTime.getOnDutyTimeAfterLimit())) {
                            Date afterDuty = DateTools.parse(today + " " + attendanceV2ShiftCheckTime.getOnDutyTimeAfterLimit(), DateTools.format_yyyyMMddHHmm);
                            if (nowDate.after(afterDuty)) { // 超过结束时间不能打卡
                                if (LOGGER.isDebugEnabled()) {
                                    LOGGER.debug("超过结束时间不能打卡!!!!");
                                }
                                break;
                            }
                        }
                        Date dutyTime = DateTools.parse(today + " " + attendanceV2ShiftCheckTime.getOnDutyTime(), DateTools.format_yyyyMMddHHmm);
                        String checkInResult = AttendanceV2CheckInRecord.CHECKIN_RESULT_NORMAL;
                        // 迟到
                        if (nowDate.after(dutyTime)) {
                            checkInResult = AttendanceV2CheckInRecord.CHECKIN_RESULT_Late;
                        }
                        // 严重迟到
                        if (shift.getSeriousTardinessLateMinutes() > 0) {
                            Date seriousLateTime = DateTools.addMinutes(dutyTime, shift.getSeriousTardinessLateMinutes());
                            if (nowDate.after(seriousLateTime)) {
                                checkInResult = AttendanceV2CheckInRecord.CHECKIN_RESULT_SeriousLate;
                            }
                        }
                        // 可以晚到
                        if (StringUtils.isNotEmpty(shift.getLateAndEarlyOnTime())) {
                            int minute = -1;
                            try {
                                minute = Integer.parseInt(shift.getLateAndEarlyOnTime());
                            }catch (Exception ignored) {
                            }
                            if (minute > 0) {
                                Date lateAndEarlyOnTime = DateTools.addMinutes(dutyTime, minute);
                                if (nowDate.before(lateAndEarlyOnTime)) {
                                    checkInResult = AttendanceV2CheckInRecord.CHECKIN_RESULT_NORMAL;
                                }
                            }
                        }
                        AttendanceV2CheckInRecord record = saveCheckInRecord(emc, effectivePerson.getDistinguishedName(), wi.getCheckInType(), group, shift, nowDate, today, wi, checkInResult, workPlace);
                        Wo wo = new Wo();
                        wo.setCheckInResult(checkInResult);
                        wo.setRecordDate(nowDate);
                        wo.setCheckInRecordId(record.getId());
                        result.setData(wo);
                        break;
                    }
                } else if (wi.getCheckInType().equals(AttendanceV2CheckInRecord.OffDuty)) {
                    AttendanceV2CheckInRecord offDutyRecord = hasCheckedRecord(recordList, index, AttendanceV2CheckInRecord.OffDuty);
                    if (offDutyRecord == null) {
                        if (StringUtils.isNotEmpty(attendanceV2ShiftCheckTime.getOffDutyTimeBeforeLimit())) {
                            Date beforeOffDuty = DateTools.parse(today + " " + attendanceV2ShiftCheckTime.getOffDutyTimeBeforeLimit(), DateTools.format_yyyyMMddHHmm);
                            if (nowDate.before(beforeOffDuty)) { // 不到开始时间不能打卡
                                if (LOGGER.isDebugEnabled()) {
                                    LOGGER.debug("不到开始时间不能打卡!!!! 下班");
                                }
                                break;
                            }
                        }
                        if (StringUtils.isNotEmpty(attendanceV2ShiftCheckTime.getOffDutyTimeAfterLimit())) {
                            Date afterOffDuty = DateTools.parse(today + " " + attendanceV2ShiftCheckTime.getOffDutyTimeAfterLimit(), DateTools.format_yyyyMMddHHmm);
                            if (nowDate.after(afterOffDuty)) { // 超过结束时间不能打卡
                                if (LOGGER.isDebugEnabled()) {
                                    LOGGER.debug("超过结束时间不能打卡!!!! 下班");
                                }
                                break;
                            }
                        }
                        Date offDutyTime = DateTools.parse(today + " " + attendanceV2ShiftCheckTime.getOffDutyTime(), DateTools.format_yyyyMMddHHmm);
                        String checkInResult = AttendanceV2CheckInRecord.CHECKIN_RESULT_NORMAL;
                        // 早退
                        if (nowDate.before(offDutyTime)) {
                            checkInResult = AttendanceV2CheckInRecord.CHECKIN_RESULT_Early;
                        }
                        // 可以早走
                        if (StringUtils.isNotEmpty(shift.getLateAndEarlyOffTime())) {
                            int minute = -1;
                            try {
                                minute = Integer.parseInt(shift.getLateAndEarlyOffTime());
                            }catch (Exception e) {
                            }
                            if (minute > 0) {
                                Date lateAndEarlyOffTime = DateTools.addMinutes(offDutyTime, -minute);
                                if (nowDate.after(lateAndEarlyOffTime)) {
                                    checkInResult = AttendanceV2CheckInRecord.CHECKIN_RESULT_NORMAL;
                                }
                            }
                        }
                        AttendanceV2CheckInRecord record = saveCheckInRecord(emc, effectivePerson.getDistinguishedName(), wi.getCheckInType(), group, shift, nowDate, today, wi, checkInResult, workPlace);
                        Wo wo = new Wo();
                        wo.setCheckInResult(checkInResult);
                        wo.setRecordDate(nowDate);
                        wo.setCheckInRecordId(record.getId());
                        result.setData(wo);
                        break;
                    }
                }
                index++;
            }
            
        }
        if (result.getData() == null) {
            throw new ExceptionNoCheckInResult();
        }

        return result;
    }


    private AttendanceV2CheckInRecord saveCheckInRecord(EntityManagerContainer emc, String person, String dutyType,
                                                        AttendanceV2Group group, AttendanceV2Shift shift,
                                                        Date nowDate, String today, Wi wi, String checkInResult, AttendanceV2WorkPlace workPlace) throws Exception {
        AttendanceV2CheckInRecord noCheckRecord = new AttendanceV2CheckInRecord();
        noCheckRecord.setCheckInType(dutyType);
        noCheckRecord.setCheckInResult(checkInResult);
        noCheckRecord.setUserId(person);
        noCheckRecord.setRecordDate(nowDate);
        noCheckRecord.setRecordDateString(today);
        noCheckRecord.setSourceType(AttendanceV2CheckInRecord.SOURCE_TYPE_USER_CHECK);
        noCheckRecord.setSourceDevice(wi.getSourceDevice());
        noCheckRecord.setDescription(wi.getDescription());
        if (workPlace != null) {
            noCheckRecord.setWorkPlaceId(workPlace.getId());
            noCheckRecord.setPlaceName(workPlace.getPlaceName());
        }
        noCheckRecord.setFieldWork(wi.getFieldWork());
        noCheckRecord.setSignDescription(wi.getSignDescription());
        noCheckRecord.setLatitude(wi.getLatitude());
        noCheckRecord.setLongitude(wi.getLongitude());
        noCheckRecord.setRecordAddress(wi.getRecordAddress());
        noCheckRecord.setGroupId(group.getId());
        noCheckRecord.setGroupName(group.getGroupName());
        noCheckRecord.setShiftId(shift.getId());
        noCheckRecord.setShiftName(shift.getShiftName());
        emc.beginTransaction(AttendanceV2CheckInRecord.class);
        emc.persist(noCheckRecord, CheckPersistType.all);
        emc.commit();
        return noCheckRecord;
    }


    public static class Wo extends GsonPropertyObject {
        @FieldDescribe("打卡结果id")
        private String checkInRecordId;
        @FieldDescribe("打卡结果")
        private String checkInResult;
        @FieldDescribe("打卡记录日期")
        private Date recordDate;

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

    public static class Wi extends GsonPropertyObject {

        @FieldDescribe("打卡类型，OnDuty OffDuty")
        private String checkInType;

        @FieldDescribe("打卡工作场所id，范围内打卡需传入")
        private String workPlaceId;

        @FieldDescribe("是否外勤打卡.")
        private Boolean isFieldWork;

        @FieldDescribe("外勤打卡说明")
        private String signDescription;

        @FieldDescribe("来源设备：Mac|Windows|IOS|Android|其他")
        private String sourceDevice;

        @FieldDescribe("其他说明备注")
        private String description;

        @FieldDescribe("当前位置经度")
        private String longitude;

        @FieldDescribe("当前位置纬度")
        private String latitude;

        @FieldDescribe("当前位置地点描述")
        private String recordAddress;


        public String getCheckInType() {
            return checkInType;
        }

        public void setCheckInType(String checkInType) {
            this.checkInType = checkInType;
        }

        public String getWorkPlaceId() {
            return workPlaceId;
        }

        public void setWorkPlaceId(String workPlaceId) {
            this.workPlaceId = workPlaceId;
        }

        public Boolean getFieldWork() {
            return isFieldWork;
        }

        public void setFieldWork(Boolean fieldWork) {
            isFieldWork = fieldWork;
        }

        public String getSignDescription() {
            return signDescription;
        }

        public void setSignDescription(String signDescription) {
            this.signDescription = signDescription;
        }

        public String getSourceDevice() {
            return sourceDevice;
        }

        public void setSourceDevice(String sourceDevice) {
            this.sourceDevice = sourceDevice;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getRecordAddress() {
            return recordAddress;
        }

        public void setRecordAddress(String recordAddress) {
            this.recordAddress = recordAddress;
        }
    }
}
