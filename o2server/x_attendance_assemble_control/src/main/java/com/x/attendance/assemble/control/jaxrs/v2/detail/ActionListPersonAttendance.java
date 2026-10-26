package com.x.attendance.assemble.control.jaxrs.v2.detail;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionWithMessage;
import com.x.attendance.assemble.control.jaxrs.v2.detail.model.RecordWo;
import com.x.attendance.entity.v2.AttendanceV2AppealInfo;
import com.x.attendance.entity.v2.AttendanceV2CheckInRecord;
import com.x.attendance.entity.v2.AttendanceV2Detail;
import com.x.attendance.entity.v2.AttendanceV2LeaveData;
import com.x.attendance.entity.v2.AttendanceV2LeaveRequest;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ActionListPersonAttendance extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionListPersonAttendance.class);

    ActionResult<List<Wo>> execute(JsonElement jsonElement) throws Exception {
        ActionResult<List<Wo>> result = new ActionResult<>();
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            if (wi == null || wi.getFilterList() == null || wi.getFilterList().isEmpty()) {
                throw new ExceptionEmptyParameter("过滤人员或组织");
            }
            Business business = new Business(emc);
            List<String> userList = new ArrayList<>();
            for (String f : wi.getFilterList()) {
                analysisPerson(userList, f, business);
            }
            if (userList.isEmpty()) {
                throw new ExceptionWithMessage("当前查询条件没有找到人员信息！");
            }
            String queryDate = wi.getQueryDate();
            if (StringUtils.isEmpty(queryDate)) {
                queryDate = DateTools.format(new Date(), DateTools.format_yyyyMMdd);
            } else {
                DateTools.parse(queryDate, DateTools.format_yyyyMMdd);
            }
            List<Wo> wos = new ArrayList<>();
            Set<String> userSet = new LinkedHashSet<>(userList);
            for (String person : userSet) {
                Wo wo = new Wo();
                wo.setPerson(person);
                List<AttendanceV2Detail> detailList = business.getAttendanceV2ManagerFactory()
                        .listDetailWithPersonAndDate(person, queryDate);
                if (detailList != null && !detailList.isEmpty()) {
                    wo.setDetail(detailList.get(0));
                }
                List<AttendanceV2CheckInRecord> recordList = business.getAttendanceV2ManagerFactory()
                        .listRecordWithPersonAndDate(person, queryDate);
                List<RecordWo> recordWos = RecordWo.copier.copy(recordList);
                fillRecordData(emc, recordWos);
                wo.setRecordList(recordWos);
                wos.add(wo);
            }
            result.setData(wos);
            result.setCount((long) wos.size());
        }

        return result;
    }

    private void analysisPerson(List<String> userList, String filter, Business business) throws Exception {
        if (StringUtils.isEmpty(filter)) {
            return;
        }
        if (filter.endsWith("@U")) { // 组织转化成人员列表 不递归
            List<String> users = business.organization().person().listWithUnitSubDirect(filter);
            if (users != null && !users.isEmpty()) {
                userList.addAll(users);
            }
        } else if (filter.endsWith("@P")) {
            userList.add(filter);
        }
    }

    private void fillRecordData(EntityManagerContainer emc, List<RecordWo> recordWos) {
        if (recordWos == null || recordWos.isEmpty()) {
            return;
        }
        for (RecordWo recordWo : recordWos) {
            try {
                if (StringUtils.isNotEmpty(recordWo.getLeaveDataId())) {
                    AttendanceV2LeaveData leaveData = emc.find(recordWo.getLeaveDataId(), AttendanceV2LeaveData.class);
                    if (leaveData != null) {
                        recordWo.setLeaveData(leaveData);
                    }
                }
                if (StringUtils.isNotEmpty(recordWo.getAppealId())) {
                    AttendanceV2AppealInfo appealData = emc.find(recordWo.getAppealId(), AttendanceV2AppealInfo.class);
                    if (appealData != null) {
                        recordWo.setAppealData(appealData);
                    }
                }
                if (StringUtils.isNotEmpty(recordWo.getRequestDataId())) {
                    AttendanceV2LeaveRequest leaveRequest = emc.find(recordWo.getRequestDataId(),
                            AttendanceV2LeaveRequest.class);
                    if (leaveRequest != null) {
                        recordWo.setLeaveRequest(leaveRequest);
                    }
                }
            } catch (Exception e) {
                LOGGER.warn("fill attendance record extend data error, record id:{}.", recordWo.getId(), e);
            }
        }
    }

    public static class Wo extends GsonPropertyObject {

        @FieldDescribe("人员标识")
        private String person;

        @FieldDescribe("考勤统计")
        private AttendanceV2Detail detail;

        @FieldDescribe("打卡记录")
        private List<RecordWo> recordList;

        public String getPerson() {
            return person;
        }

        public void setPerson(String person) {
            this.person = person;
        }

        public AttendanceV2Detail getDetail() {
            return detail;
        }

        public void setDetail(AttendanceV2Detail detail) {
            this.detail = detail;
        }

        public List<RecordWo> getRecordList() {
            return recordList;
        }

        public void setRecordList(
                List<RecordWo> recordList) {
            this.recordList = recordList;
        }
    }

    public static class Wi extends GsonPropertyObject {

        @FieldDescribe("过滤人员或组织，组织只支持单层: 用户或组织的DN，如xxx@xxx@P、xxx@xxx@U")
        private List<String> filterList;

        @FieldDescribe("查询日期 yyyy-MM-dd")
        private String queryDate;

        public List<String> getFilterList() {
            return filterList;
        }

        public void setFilterList(List<String> filterList) {
            this.filterList = filterList;
        }

        public String getQueryDate() {
            return queryDate;
        }

        public void setQueryDate(String queryDate) {
            this.queryDate = queryDate;
        }
    }

}
