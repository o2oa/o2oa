package com.x.attendance.assemble.control.jaxrs.v2.mobile;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionNotExistObject;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionWithMessage;
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

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
            if (StringUtils.isEmpty(wi.getRecordId())) {
                throw new ExceptionEmptyParameter("打卡对象id");
            }
            if (StringUtils.isEmpty(wi.getCheckInType()) || (!wi.getCheckInType().equals(AttendanceV2CheckInRecord.OnDuty) && !wi.getCheckInType().equals(AttendanceV2CheckInRecord.OffDuty))) {
                throw new ExceptionEmptyParameter("打卡类型");
            }
            if (StringUtils.isEmpty(wi.getLatitude()) || StringUtils.isEmpty(wi.getLongitude())) {
                throw new ExceptionEmptyParameter("经纬度");
            }
            if ((BooleanUtils.isFalse(wi.getFieldWork())) && StringUtils.isEmpty(wi.getWorkPlaceId())) {
                throw new ExceptionEmptyParameter("打卡工作场所id");
            }
            // 查询当前用户的考勤组
            Business business = new Business(emc);
            List<AttendanceV2Group> groups = business.getAttendanceV2ManagerFactory().listGroupWithPerson(effectivePerson.getDistinguishedName());
            if (groups == null || groups.isEmpty()) {
                throw new ExceptionNotExistObject("考勤组信息");
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
            AttendanceV2CheckInRecord record = emc.find(wi.getRecordId(), AttendanceV2CheckInRecord.class);
            if (record == null) {
                throw new ExceptionNotExistObject("打卡记录");
            }
            if (!effectivePerson.getDistinguishedName().equals(record.getUserId())) {
                throw new ExceptionWithMessage("用户不匹配，无法打卡！");
            }
            // 极速打卡不能更新已经打卡的数据
            if (AttendanceV2CheckInRecord.SOURCE_TYPE_FAST_CHECK.equals(wi.getSourceType()) && !AttendanceV2CheckInRecord.CHECKIN_RESULT_PreCheckIn.equals(record.getCheckInResult())) {
                throw new ExceptionCannotFastCheckIn();
            }
            String checkInResult = AttendanceV2CheckInRecord.CHECKIN_RESULT_NORMAL;
            // 是否有班次信息
            if (StringUtils.isNotEmpty(record.getShiftId())) {
                AttendanceV2Shift shift = business.getAttendanceV2ManagerFactory().pick(record.getShiftId(), AttendanceV2Shift.class);
                if (shift == null) {
                    throw new ExceptionNotExistObject("班次对象");
                }
                if (StringUtils.isNotEmpty(record.getPreDutyTimeBeforeLimit())) {
                    Date beforeOnDuty = DateTools.parse(today + " " + record.getPreDutyTimeBeforeLimit(), DateTools.format_yyyyMMddHHmm);
                    if (nowDate.before(beforeOnDuty)) { // 不到开始时间不能打卡
                        throw new ExceptionTimeError("不到开始时间不能打卡");
                    }
                }
                if (StringUtils.isNotEmpty(record.getPreDutyTimeAfterLimit())) {
                    Date afterDuty = DateTools.parse(today + " " + record.getPreDutyTimeAfterLimit(), DateTools.format_yyyyMMddHHmm);
                    if (nowDate.after(afterDuty)) { // 超过结束时间不能打卡
                        throw new ExceptionTimeError("超过结束时间不能打卡");
                    }
                }
                // 上班打卡
                if (record.getCheckInType().equals(AttendanceV2CheckInRecord.OnDuty)) {

                    Date dutyTime = DateTools.parse(today + " " + record.getPreDutyTime(), DateTools.format_yyyyMMddHHmm);
                    checkInResult = AttendanceV2CheckInRecord.CHECKIN_RESULT_NORMAL;
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

                } else if (record.getCheckInType().equals(AttendanceV2CheckInRecord.OffDuty)) {
                    Date offDutyTime = DateTools.parse(today + " " + record.getPreDutyTime(), DateTools.format_yyyyMMddHHmm);
                    checkInResult = AttendanceV2CheckInRecord.CHECKIN_RESULT_NORMAL;
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
                    // 工作时长检查
                    if (checkInResult.equals(AttendanceV2CheckInRecord.CHECKIN_RESULT_NORMAL) && BooleanUtils.isTrue(shift.getNeedLimitWorkTime())  && shift.getWorkTime() > 0) {
                        // 当前打卡的  recordString  查询对应的打卡记录，因为有可能跨天 需要查同一组打卡记录
                        List<AttendanceV2CheckInRecord> recordList = business.getAttendanceV2ManagerFactory().listRecordWithPersonAndDate(effectivePerson.getDistinguishedName(), record.getRecordDateString());
                        if (recordList == null || recordList.isEmpty()) {
                            throw new ExceptionNoTodayRecordList();
                        }
                        // 确定是最后一条打卡
                        if (record.getId().equals(recordList.get(recordList.size()-1).getId())) {
                            long realWorkTime = 0;
                             // 上班打卡
                            List<AttendanceV2CheckInRecord> onDutyList = recordList.stream().filter(
                                            (r) -> r.getCheckInType().equals(AttendanceV2CheckInRecord.OnDuty)  )
                                    .sorted(Comparator.comparing(AttendanceV2CheckInRecord::getRecordDate)).collect(Collectors.toList());
                            // 下班打卡
                            List<AttendanceV2CheckInRecord> offDutyList = recordList.stream().filter(
                                            (r) -> r.getCheckInType().equals(AttendanceV2CheckInRecord.OffDuty) )
                                    .sorted(Comparator.comparing(AttendanceV2CheckInRecord::getRecordDate)).collect(Collectors.toList());
                            for (int i = 0; i < onDutyList.size(); i++) {
                                AttendanceV2CheckInRecord onDuty = onDutyList.get(i);
                                AttendanceV2CheckInRecord offDuty = offDutyList.get(i);
                                if (offDuty.getId().equals(record.getId())) {
                                    realWorkTime += (nowDate.getTime() - onDuty.getRecordDate().getTime());
                                } else {
                                    realWorkTime += (offDuty.getRecordDate().getTime() - onDuty.getRecordDate().getTime());
                                }
                            }
                            if (realWorkTime < shift.getWorkTime()) { // 工作时长不足 标记未早退
                                checkInResult = AttendanceV2CheckInRecord.CHECKIN_RESULT_Early;
                                LOGGER.info("时长不足，标记为早退，person {} , realWorkTime {} , needWorkTime {}", effectivePerson.getDistinguishedName(), ""+realWorkTime, ""+shift.getWorkTime());
                            }
                        }
                    }

                }
            }

            // 更新打卡
            emc.beginTransaction(AttendanceV2CheckInRecord.class);
            record.setRecordDate(nowDate);
            if (StringUtils.isNotEmpty(wi.getSourceType())) {
                record.setSourceType(wi.getSourceType());
            } else {
                record.setSourceType(AttendanceV2CheckInRecord.SOURCE_TYPE_USER_CHECK);
            }
            record.setCheckInResult(checkInResult);
            record.setSourceDevice(wi.getSourceDevice());
            if (StringUtils.isNotEmpty(wi.getDescription())) {
                record.setDescription(wi.getDescription());
            } else {
                record.setDescription("");
            }
            if (workPlace != null) {
                record.setWorkPlaceId(workPlace.getId());
                record.setPlaceName(workPlace.getPlaceName());
            }
            record.setFieldWork(wi.getFieldWork());
            record.setSignDescription(wi.getSignDescription());
            record.setLatitude(wi.getLatitude());
            record.setLongitude(wi.getLongitude());
            record.setRecordAddress(wi.getRecordAddress());
            emc.check(record, CheckPersistType.all);
            emc.commit();
            LOGGER.info("checkIn 打卡 数据记录， 打卡人员：{}, 打卡日期：{}, 打卡结果：{} ", effectivePerson.getDistinguishedName(), today, checkInResult);
            // 异常数据
            generateAppealInfo(record, groups.get(0).getFieldWorkMarkError(), emc, business);
            Wo wo = new Wo();
            wo.setCheckInResult(checkInResult);
            wo.setRecordDate(nowDate);
            wo.setCheckInRecordId(record.getId());
            result.setData(wo);
        }
        if (result.getData() == null) {
            throw new ExceptionNoCheckInResult();
        }
        return result;
    }

    /**
     * 异常
     * @param record
     * @param emc
     * @param business
     */
    private void generateAppealInfo(AttendanceV2CheckInRecord record,boolean fieldWorkMarkError, EntityManagerContainer emc, Business business) {
        try {
            if (record != null && record.checkResultException(fieldWorkMarkError)) {
                AttendanceV2Config config = null;
                List<AttendanceV2Config> configs = emc.listAll(AttendanceV2Config.class);
                if (configs != null && !configs.isEmpty()) {
                    config = configs.get(0);
                }
                if (config == null || !config.getAppealEnable()) {
                    return;
                }
                List<AttendanceV2AppealInfo> appealList = business.getAttendanceV2ManagerFactory().listAppealInfoWithRecordId(record.getId());
                if (appealList != null && !appealList.isEmpty()) {
                    LOGGER.info("当前打卡记录已经有申诉数据存在，不需要重复生成！{}", record.getId());
                    return;
                }
                AttendanceV2AppealInfo appealInfo = new AttendanceV2AppealInfo();
                appealInfo.setRecordId(record.getId());
                appealInfo.setRecordDateString(record.getRecordDateString());
                appealInfo.setRecordDate(record.getRecordDate());
                appealInfo.setUserId(record.getUserId());
                emc.beginTransaction(AttendanceV2AppealInfo.class);
                emc.persist(appealInfo, CheckPersistType.all);
                emc.commit();
                LOGGER.info("生成对应的异常打卡申请数据, {}", appealInfo.toString());
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
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

        @FieldDescribe("打卡对象id")
        private String recordId;

        @FieldDescribe("打卡类型，OnDuty OffDuty")
        private String checkInType;

        @FieldDescribe("打卡工作场所id，范围内打卡需传入")
        private String workPlaceId;

        @FieldDescribe("是否外勤打卡.")
        private Boolean fieldWork;

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

        @FieldDescribe("打卡数据来源： USER_CHECK（用户打卡） FAST_CHECK（极速打卡） ")
        private String sourceType;

        public String getSourceType() {
            return sourceType;
        }

        public void setSourceType(String sourceType) {
            this.sourceType = sourceType;
        }

        public String getRecordId() {
            return recordId;
        }

        public void setRecordId(String recordId) {
            this.recordId = recordId;
        }

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
            return fieldWork;
        }

        public void setFieldWork(Boolean fieldWork) {
            this.fieldWork = fieldWork;
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
