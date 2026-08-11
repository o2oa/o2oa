package com.x.attendance.assemble.control.jaxrs.v2.detail;

import com.x.attendance.entity.v2.AttendanceV2LeaveRequest;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.AttendanceV2Helper;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionWithMessage;
import com.x.attendance.entity.v2.AttendanceV2CheckInRecord;
import com.x.attendance.entity.v2.AttendanceV2Detail;
import com.x.attendance.entity.v2.AttendanceV2LeaveData;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

/**
 * Created by fancyLou on 2023/2/27.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionListByPage extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionListByPage.class);


    ActionResult<List<Wo>> execute(Integer page, Integer size, JsonElement jsonElement) throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(" ActionListByPage page:{}, size:{}.", page, size);
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            if (wi == null || StringUtils.isEmpty(wi.getStartDate()) || StringUtils.isEmpty(wi.getEndDate())) {
                throw new ExceptionEmptyParameter("日期");
            }
            ActionResult<List<Wo>> result = new ActionResult<>();
            Business business = new Business(emc);
            Integer adjustPage = this.adjustPage(page);
            Integer adjustPageSize = this.adjustSize(size);
            List<String> userList = null;
            if (wi.getFilterList() != null && !wi.getFilterList().isEmpty()) {
                userList = new ArrayList<>();
                for (String f : wi.getFilterList()) {
                    AttendanceV2Helper.analysisFilterToPersonList(userList, f, business, wi.getRecursive());
                }
                userList = new ArrayList<>(new LinkedHashSet<>(userList));
                if (userList.isEmpty()) {
                    throw new ExceptionWithMessage("当前查询条件没有找到人员信息！");
                }
            }
            List<AttendanceV2Detail> list = business.getAttendanceV2ManagerFactory().listDetailByPage(adjustPage,
                    adjustPageSize, wi.getUserId(), userList, wi.getStartDate(), wi.getEndDate());
            List<Wo> wos =  Wo.copier.copy(list);
            if (wos != null && !wos.isEmpty()) {
                for (Wo detail : wos) {
                    List<String> ids = detail.getRecordIdList();
                    if (ids != null && !ids.isEmpty()) {
                        List<WoRecord> recordList = new ArrayList<>();
                        for (String id : ids) {
                            AttendanceV2CheckInRecord record = emc.find(id, AttendanceV2CheckInRecord.class);
                            if (record != null) {
                                WoRecord woRecord = WoRecord.copier.copy(record);
                                try {
                                    if (StringUtils.isNotEmpty(woRecord.getLeaveDataId())) {
                                        AttendanceV2LeaveData leaveData = emc.find(woRecord.getLeaveDataId(), AttendanceV2LeaveData.class);
                                        if (leaveData != null) {
                                            woRecord.setLeaveData(leaveData);
                                        }
                                    }
                                    if (StringUtils.isNotEmpty(woRecord.getRequestDataId())) {
                                        AttendanceV2LeaveRequest leaveRequest = emc.find(woRecord.getRequestDataId(), AttendanceV2LeaveRequest.class);
                                        if (leaveRequest != null) {
                                            woRecord.setLeaveRequest(leaveRequest);
                                        }
                                    }
                                } catch (Exception ignore) {}
                                recordList.add(woRecord);
                            }
                        }
                        detail.setRecordList(recordList);
                    }
                }
            }
            result.setData(wos);
            result.setCount(business.getAttendanceV2ManagerFactory()
                    .detailCount(wi.getUserId(), userList, wi.getStartDate(), wi.getEndDate()));
            return result;
        }

    }

    public static class Wi extends GsonPropertyObject {


        private static final long serialVersionUID = 5957606815096636450L;


        @FieldDescribe("用户标识")
        private String userId;

        @FieldDescribe("过滤人员或组织，组织默认递归: 用户或组织的DN，如xxx@xxx@P、xxx@xxx@U")
        private List<String> filterList;

        @FieldDescribe("过滤组织是否递归查询下级组织人员，默认true，false时仅查询当前组织直属人员")
        private Boolean recursive;

        @FieldDescribe("开始日期")
        private String startDate;
        @FieldDescribe("结束日期")
        private String endDate;


        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public List<String> getFilterList() {
            return filterList;
        }

        public void setFilterList(List<String> filterList) {
            this.filterList = filterList;
        }

        public Boolean getRecursive() {
            return recursive;
        }

        public void setRecursive(Boolean recursive) {
            this.recursive = recursive;
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

    public static class Wo extends AttendanceV2Detail {
        static WrapCopier<AttendanceV2Detail, Wo> copier = WrapCopierFactory.wo(AttendanceV2Detail.class, Wo.class, null,
                JpaObject.FieldsInvisible);

        @FieldDescribe("打卡记录")
        private List<WoRecord> recordList;

        public List<WoRecord> getRecordList() {
            return recordList;
        }

        public void setRecordList(List<WoRecord> recordList) {
            this.recordList = recordList;
        }
    }

    public static class WoRecord extends AttendanceV2CheckInRecord {
        @FieldDescribe("外出记录")
        private AttendanceV2LeaveData leaveData;
        @FieldDescribe("请假记录")
        private AttendanceV2LeaveRequest leaveRequest;

        static WrapCopier<AttendanceV2CheckInRecord, WoRecord> copier = WrapCopierFactory.wo(AttendanceV2CheckInRecord.class, WoRecord.class, null,
                JpaObject.FieldsInvisible);

        public AttendanceV2LeaveData getLeaveData() {
            return leaveData;
        }

        public void setLeaveData(AttendanceV2LeaveData leaveData) {
            this.leaveData = leaveData;
        }

        public AttendanceV2LeaveRequest getLeaveRequest() {
            return leaveRequest;
        }

        public void setLeaveRequest(AttendanceV2LeaveRequest leaveRequest) {
            this.leaveRequest = leaveRequest;
        }
    }
}
