package com.x.attendance.assemble.control.jaxrs.v2.my;

import com.x.attendance.entity.v2.AttendanceV2Detail;
import com.x.attendance.entity.v2.AttendanceV2LeaveRequest;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.entity.v2.AttendanceV2AppealInfo;
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
import com.x.base.core.project.tools.DateTools;

public class ActionListCheckInRecordWithDate extends BaseAction {

    ActionResult<WoDetail> execute(EffectivePerson person, JsonElement jsonElement) throws Exception {
        ActionResult<WoDetail> result = new ActionResult<>();
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            String date = wi.getDate();
            if (StringUtils.isEmpty(date)) {
                throw new ExceptionEmptyParameter("日期");
            }
            DateTools.parse(date, DateTools.format_yyyyMMdd);
            WoDetail detail = new WoDetail();
            Business business = new Business(emc);
            List<AttendanceV2Detail> dList = business.getAttendanceV2ManagerFactory().listDetailWithPersonAndDate(person.getDistinguishedName(), date);
            if (dList != null && !dList.isEmpty()) {
                detail.setDetail(dList.get(0));
            }
            List<AttendanceV2CheckInRecord> list = business.getAttendanceV2ManagerFactory()
                    .listRecordWithPersonAndDate(person.getDistinguishedName(), date);
            List<Wo> wos = Wo.copier.copy(list);
            for (Wo wo : wos) {
                if (StringUtils.isNotEmpty(wo.getLeaveDataId())) {
                    AttendanceV2LeaveData leaveData = emc.find(wo.getLeaveDataId(), AttendanceV2LeaveData.class);
                    if (leaveData != null) {
                        wo.setLeaveData(leaveData);
                    }
                }
                if (StringUtils.isNotEmpty(wo.getRequestDataId())) {
                    AttendanceV2LeaveRequest leaveRequest = emc.find(wo.getRequestDataId(), AttendanceV2LeaveRequest.class);
                    if (leaveRequest != null) {
                        wo.setLeaveRequest(leaveRequest);
                    }
                }
                if (StringUtils.isNotEmpty(wo.getAppealId())) {
                    AttendanceV2AppealInfo appealData = emc.find(wo.getAppealId(), AttendanceV2AppealInfo.class);
                    if (appealData != null) {
                        wo.setAppealData(appealData);
                    }
                }
            }
            detail.setRecordList(wos);
            result.setData(detail);
            return result;
        }
    }

    public static class Wi extends GsonPropertyObject {

        private static final long serialVersionUID = 2764109224722244861L;

        @FieldDescribe("查询日期，格式 yyyy-MM-dd")
        private String date;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

    }

    public static class WoDetail extends GsonPropertyObject {

        private static final long serialVersionUID = 2764109224722244861L;

        @FieldDescribe("打卡记录")
        private List<Wo> recordList;

        @FieldDescribe("考勤详细数据")
        private AttendanceV2Detail detail;

        public List<Wo> getRecordList() {
            return recordList;
        }

        public void setRecordList(
                List<Wo> recordList) {
            this.recordList = recordList;
        }

        public AttendanceV2Detail getDetail() {
            return detail;
        }

        public void setDetail(AttendanceV2Detail detail) {
            this.detail = detail;
        }
    }

    public static class Wo extends AttendanceV2CheckInRecord {

        private static final long serialVersionUID = -4965896902893222132L;

        static WrapCopier<AttendanceV2CheckInRecord, Wo> copier = WrapCopierFactory.wo(AttendanceV2CheckInRecord.class,
                Wo.class, null, JpaObject.FieldsInvisible);

        @FieldDescribe("外出记录")
        private AttendanceV2LeaveData leaveData;
        @FieldDescribe("请假记录")
        private AttendanceV2LeaveRequest leaveRequest;
        @FieldDescribe("申诉记录")
        private AttendanceV2AppealInfo appealData;

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

        public AttendanceV2LeaveRequest getLeaveRequest() {
            return leaveRequest;
        }

        public void setLeaveRequest(AttendanceV2LeaveRequest leaveRequest) {
            this.leaveRequest = leaveRequest;
        }
    }
}
