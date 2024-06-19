package com.x.attendance.assemble.control.jaxrs.v2.record;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.v2.AttendanceV2CheckInRecord;
import com.x.attendance.entity.v2.AttendanceV2LeaveData;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;

/**
 * Created by fancyLou on 2023/2/15.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionListByPage extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionListByPage.class);

    ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size, JsonElement jsonElement)
            throws Exception {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("execute:{}, page:{}, size:{}.", effectivePerson.getDistinguishedName(), page, size);
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<List<Wo>> result = new ActionResult<>();
            Business business = new Business(emc);
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            if (wi == null) {
                wi = new Wi();
            }
            Integer adjustPage = this.adjustPage(page);
            Integer adjustPageSize = this.adjustSize(size);
            Date start = null;
            Date end = null;
            if (StringUtils.isNotEmpty(wi.getStartDate()) && StringUtils.isNotEmpty(wi.getEndDate())) {
                start = DateTools.parseDateTime(wi.getStartDate() + " 00:00:00");
                end = DateTools.parseDateTime(wi.getEndDate() + " 23:59:59");
            }
            List<AttendanceV2CheckInRecord> list = business.getAttendanceV2ManagerFactory().listRecordByPage(adjustPage,
                    adjustPageSize, wi.getUserId(), start, end, wi.getSourceType(), wi.getCheckInResult(),
                    wi.getCheckInType(), wi.getFieldWork());
            List<Wo> wos = Wo.copier.copy(list);
            for (Wo wo : wos) {
                try {
                    if (StringUtils.isNotEmpty(wo.getLeaveDataId())) {
                        AttendanceV2LeaveData leaveData = emc.find(wo.getLeaveDataId(), AttendanceV2LeaveData.class);
                        if (leaveData != null) {
                            wo.setLeaveData(leaveData);
                        }
                    }
                } catch (Exception ignore) {}
            }
            result.setData(wos);
            result.setCount(business.getAttendanceV2ManagerFactory().recordCount(wi.getUserId(), start, end, wi.getSourceType(), wi.getCheckInResult(),
                    wi.getCheckInType(), wi.getFieldWork()));
            return result;
        }
    }

    public static class Wi extends GsonPropertyObject {


        private static final long serialVersionUID = 4227642755086093795L;
        @FieldDescribe("打卡的用户标识")
        private String userId;
        @FieldDescribe("打卡记录开始日期: YYYY-MM-dd")
        private String startDate;
        @FieldDescribe("打卡记录结束日期: YYYY-MM-dd")
        private String endDate;
        @FieldDescribe("打卡数据来源：USER_CHECK|AUTO_CHECK|FAST_CHECK|SYSTEM_IMPORT")
        private String sourceType;
        @FieldDescribe("打卡结果: Normal|Early|Late|SeriousLate|NotSigned")
        private String checkInResult;
        @FieldDescribe("考勤类型: OnDuty|OffDuty")
        private String checkInType;
        @FieldDescribe("是否外勤打卡.")
        private Boolean fieldWork;

        public String getSourceType() {
            return sourceType;
        }

        public void setSourceType(String sourceType) {
            this.sourceType = sourceType;
        }

        public String getCheckInResult() {
            return checkInResult;
        }

        public void setCheckInResult(String checkInResult) {
            this.checkInResult = checkInResult;
        }

        public String getCheckInType() {
            return checkInType;
        }

        public void setCheckInType(String checkInType) {
            this.checkInType = checkInType;
        }

        public Boolean getFieldWork() {
            return fieldWork;
        }

        public void setFieldWork(Boolean fieldWork) {
            this.fieldWork = fieldWork;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
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


    public static class Wo extends AttendanceV2CheckInRecord {

        @FieldDescribe("外出请假记录")
        private AttendanceV2LeaveData leaveData;

        public AttendanceV2LeaveData getLeaveData() {
            return leaveData;
        }

        public void setLeaveData(AttendanceV2LeaveData leaveData) {
            this.leaveData = leaveData;
        }

        static WrapCopier<AttendanceV2CheckInRecord, Wo> copier = WrapCopierFactory.wo(AttendanceV2CheckInRecord.class, Wo.class, null,
                JpaObject.FieldsInvisible);
    }
}
