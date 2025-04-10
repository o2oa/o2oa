package com.x.attendance.assemble.control.jaxrs.v2.mobile;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.ThisApplication;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.WoGroupShift;
import com.x.attendance.entity.v2.AttendanceV2CheckInRecord;
import com.x.attendance.entity.v2.AttendanceV2Group;
import com.x.attendance.entity.v2.AttendanceV2WorkPlace;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * 当前是查询打卡数据的接口
 * 在打开之前必须先查询这个接口，根据这个接口返回的打卡结果列表数据进行打卡
 * 打卡结果checkInResult 中，PreCheckIn是预存数据需要打卡，其它是已经打卡的结果
 * Created by fancyLou on 2023/2/21.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionPreCheck extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionPreCheck.class);

    ActionResult<Wo> execute(String person) throws Exception {
        ActionResult<Wo> result = new ActionResult<>();
        if (StringUtils.isEmpty(person)) {
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
            WoGroupShift woGroupShift = business.getAttendanceV2ManagerFactory().getGroupShiftByPersonDate(person, today);
            if (woGroupShift == null || woGroupShift.getGroup() == null) {
                result.setData(cannotCheckIn("没有对应的考勤组"));
                return result;
            }
            AttendanceV2Group group = woGroupShift.getGroup();
            // 没有配置工作地址 不能打卡
            if (group.getWorkPlaceIdList() == null || group.getWorkPlaceIdList().isEmpty()) {
                result.setData(cannotCheckIn("没有配置工作地址"));
                return result;
            }
            // 处理并发的问题
            List<AttendanceV2CheckInRecord> recordList = ThisApplication.executor
                    .submit(new CallableImpl(person, group, woGroupShift.getShift(), nowDate)).get();
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
