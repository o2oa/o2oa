package com.x.attendance.assemble.control.jaxrs.v2.mobile;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionNotExistObject;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionWithMessage;
import com.x.attendance.entity.v2.AttendanceV2CheckInRecord;
import com.x.attendance.entity.v2.AttendanceV2Group;
import com.x.attendance.entity.v2.AttendanceV2WorkPlace;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

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
            // 查询打卡记录
            Date nowDate = new Date();
            checkIn(emc, business, nowDate, record, workPlace, wi, null);
            // 异常数据
            generateAppealInfo(record, groups.get(0).getFieldWorkMarkError(), emc, business);
            Wo wo = new Wo();
            wo.setCheckInResult(record.getCheckInResult());
            wo.setRecordDate(nowDate);
            wo.setCheckInRecordId(record.getId());
            result.setData(wo);
        }
        if (result.getData() == null) {
            throw new ExceptionNoCheckInResult();
        }
        return result;
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
