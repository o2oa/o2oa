package com.x.attendance.assemble.control.jaxrs.v2.my;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.detail.ExceptionDateEndBeforeStartError;
import com.x.attendance.entity.v2.AttendanceV2AppealInfo;
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
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.DateTools;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by fancyLou on 2023/3/9.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionListDetailWithDate extends BaseAction {


    ActionResult<List<Wo>> execute(EffectivePerson person, JsonElement jsonElement) throws Exception {

        ActionResult<List<Wo>> result = new ActionResult<>();
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            if (StringUtils.isEmpty(wi.getStartDate())) {
                throw new ExceptionEmptyParameter("开始日期");
            }
            if (StringUtils.isEmpty(wi.getEndDate())) {
                throw new ExceptionEmptyParameter("结束日期");
            }
            Date startDate = DateTools.parse(wi.getStartDate(), DateTools.format_yyyyMMdd); // 检查格式
            Date endDate = DateTools.parse(wi.getEndDate(), DateTools.format_yyyyMMdd); // 检查格式
            if (startDate.after(endDate)) {
                throw new ExceptionDateEndBeforeStartError();
            }
            Business business = new Business(emc);
            List<Wo> wos = new ArrayList<>();
            List<AttendanceV2Detail> list = business.getAttendanceV2ManagerFactory().listDetailWithPersonAndStartEndDate(person.getDistinguishedName(), wi.getStartDate(), wi.getEndDate());
            if (list != null && !list.isEmpty()) {
                for (AttendanceV2Detail detail : list) {
                    Wo wo = Wo.copier.copy(detail);
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
                                    if (StringUtils.isNotEmpty(woRecord.getAppealId())) {
                                        AttendanceV2AppealInfo appealData = business.entityManagerContainer().find(woRecord.getAppealId(), AttendanceV2AppealInfo.class);
                                        if (appealData != null) {
                                            woRecord.setAppealData(appealData);
                                        }
                                    }
                                } catch (Exception ignore) {}
                                recordList.add(woRecord);
                            }
                        }
                        // 如果是休息日 只有在有打卡记录的时候才提供数据，否则是无效数据
                        if (!detail.getWorkDay()) {
                            List<AttendanceV2CheckInRecord> signList = recordList.stream().filter((r) -> (!r.getCheckInResult().equals(AttendanceV2CheckInRecord.CHECKIN_RESULT_PreCheckIn) && !r.getCheckInResult().equals(AttendanceV2CheckInRecord.CHECKIN_RESULT_NotSigned)))
                                    .collect(Collectors.toList());
                            if (!signList.isEmpty()) {

                                wo.setRecordList(recordList);
                            } else { // 空数组前端好处理
                                wo.setRecordList(new ArrayList<>());
                            }
                        } else {
                            wo.setRecordList(recordList);
                        }
                    }
                    wos.add(wo);
                }
            }
            result.setData(wos);
            return result;
        }
    }


    public static class Wi extends GsonPropertyObject {

        private static final long serialVersionUID = 6842439549150552191L;
        @FieldDescribe("开始日期")
        private String startDate;
        @FieldDescribe("结束日期")
        private String endDate;


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

        private static final long serialVersionUID = 4645923067324854260L;
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
        @FieldDescribe("外出请假记录")
        private AttendanceV2LeaveData leaveData;


        @FieldDescribe("申诉记录")
        private AttendanceV2AppealInfo appealData;

        static WrapCopier<AttendanceV2CheckInRecord, WoRecord> copier = WrapCopierFactory.wo(AttendanceV2CheckInRecord.class, WoRecord.class, null,
                JpaObject.FieldsInvisible);

        public AttendanceV2LeaveData getLeaveData() {
            return leaveData;
        }

        public void setLeaveData(AttendanceV2LeaveData leaveData) {
            this.leaveData = leaveData;
        }

        public AttendanceV2AppealInfo getAppealData() {
          return appealData;
        }

        public void setAppealData(AttendanceV2AppealInfo appealData) {
          this.appealData = appealData;
        }
        
    }
}
