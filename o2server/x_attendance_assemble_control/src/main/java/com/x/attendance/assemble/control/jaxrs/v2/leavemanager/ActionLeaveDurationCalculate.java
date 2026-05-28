package com.x.attendance.assemble.control.jaxrs.v2.leavemanager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.AttendanceV2RestDayHelper;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionNotExistObject;
import com.x.attendance.assemble.control.jaxrs.v2.detail.ExceptionDateEndBeforeStartError;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.tools.DateTools;

public class ActionLeaveDurationCalculate extends BaseAction {

    ActionResult<Wo> execute(JsonElement jsonElement) throws Exception {
        ActionResult<Wo> result = new ActionResult<>();
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            if (StringUtils.isEmpty(wi.getPerson())) {
                throw new ExceptionEmptyParameter("人员标识");
            }
            if (StringUtils.isEmpty(wi.getStartDate())) {
                throw new ExceptionEmptyParameter("开始日期");
            }
            if (StringUtils.isEmpty(wi.getEndDate())) {
                throw new ExceptionEmptyParameter("结束日期");
            }
            Date startDate = DateTools.parse(wi.getStartDate(), DateTools.format_yyyyMMdd);
            Date endDate = DateTools.parse(wi.getEndDate(), DateTools.format_yyyyMMdd);
            if (startDate.after(endDate)) {
                throw new ExceptionDateEndBeforeStartError();
            }

            Business business = new Business(emc);
            Person person = business.organization().person().getObject(wi.getPerson(), true);
            if (person == null) {
                throw new ExceptionNotExistObject("人员 " + wi.getPerson());
            }

            List<String> dateList = AttendanceV2RestDayHelper.listDateRange(startDate, endDate);
            List<String> restDateList = AttendanceV2RestDayHelper.listRestDate(business, person.getDistinguishedName(), dateList);
            List<String> leaveDateList = dateList.stream().filter(date -> !restDateList.contains(date))
                    .collect(Collectors.toList());

            Wo wo = new Wo();
            wo.setTotalDays(dateList.size());
            wo.setDuration((double) leaveDateList.size());
            wo.setLeaveDateList(leaveDateList);
            wo.setRestDateList(restDateList);
            result.setData(wo);
            return result;
        }
    }

    public static class Wi extends GsonPropertyObject {

        private static final long serialVersionUID = 3972136115624287303L;

        @FieldDescribe("人员标识")
        private String person;

        @FieldDescribe("开始日期，yyyy-MM-dd")
        private String startDate;

        @FieldDescribe("结束日期，yyyy-MM-dd")
        private String endDate;

        public String getPerson() {
            return person;
        }

        public void setPerson(String person) {
            this.person = person;
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

    public static class Wo extends GsonPropertyObject {

        private static final long serialVersionUID = 2373093028785508444L;

        @FieldDescribe("自然日天数")
        private Integer totalDays = 0;

        @FieldDescribe("实际请假天数")
        private Double duration = 0.0;

        @FieldDescribe("实际请假的日期列表，yyyy-MM-dd")
        private List<String> leaveDateList = new ArrayList<>();

        @FieldDescribe("休息日的日期列表，yyyy-MM-dd")
        private List<String> restDateList = new ArrayList<>();

        public Integer getTotalDays() {
            return totalDays;
        }

        public void setTotalDays(Integer totalDays) {
            this.totalDays = totalDays;
        }

        public Double getDuration() {
            return duration;
        }

        public void setDuration(Double duration) {
            this.duration = duration;
        }

        public List<String> getLeaveDateList() {
            return leaveDateList;
        }

        public void setLeaveDateList(List<String> leaveDateList) {
            this.leaveDateList = leaveDateList;
        }

        public List<String> getRestDateList() {
            return restDateList;
        }

        public void setRestDateList(List<String> restDateList) {
            this.restDateList = restDateList;
        }
    }
}
