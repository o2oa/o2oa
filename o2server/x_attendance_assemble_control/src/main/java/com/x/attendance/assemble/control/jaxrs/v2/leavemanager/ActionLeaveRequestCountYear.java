package com.x.attendance.assemble.control.jaxrs.v2.leavemanager;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionNotExistObject;
import com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model.AttendanceV2LeaveRequestEnums.LeaveRequestStatusEnum;
import com.x.attendance.entity.v2.AttendanceV2LeaveRequest;
import com.x.attendance.entity.v2.AttendanceV2LeaveRequest_;
import com.x.attendance.entity.v2.AttendanceV2LeaveType;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.tools.DateTools;

public class ActionLeaveRequestCountYear extends BaseAction {

    ActionResult<Wo> execute(JsonElement jsonElement) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            if (StringUtils.isBlank(wi.getPerson())) {
                throw new ExceptionEmptyParameter("人员标识");
            }
            if (StringUtils.isBlank(wi.getLeaveTypeId())) {
                throw new ExceptionEmptyParameter("请假类型ID");
            }
            Business business = new Business(emc);
            Person person = business.organization().person().getObject(wi.getPerson(), true);
            if (person == null) {
                throw new ExceptionNotExistObject("人员 " + wi.getPerson());
            }
            AttendanceV2LeaveType leaveType = emc.find(wi.getLeaveTypeId(), AttendanceV2LeaveType.class);
            if (leaveType == null) {
                throw new ExceptionNotExistObject("请假类型 " + wi.getLeaveTypeId());
            }
            YearRange yearRange = currentYearRange();
            Long count = countLeaveRequest(emc, person.getDistinguishedName(), wi.getLeaveTypeId(),
                    yearRange.getStartDate(), yearRange.getEndDate());
            Wo wo = new Wo();
            wo.setPerson(person.getDistinguishedName());
            wo.setLeaveTypeId(wi.getLeaveTypeId());
            wo.setYear(yearRange.getYear());
            wo.setStartDate(DateTools.format(yearRange.getStartDate(), DateTools.format_yyyyMMdd));
            wo.setEndDate(DateTools.format(yearRange.getEndDate(), DateTools.format_yyyyMMdd));
            wo.setCount(count);
            result.setData(wo);
            result.setCount(count);
            return result;
        }
    }

    private Long countLeaveRequest(EntityManagerContainer emc, String person, String leaveTypeId, Date startDate,
            Date endDate) throws Exception {
        EntityManager em = emc.get(AttendanceV2LeaveRequest.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<AttendanceV2LeaveRequest> root = cq.from(AttendanceV2LeaveRequest.class);
        Predicate p = cb.equal(root.get(AttendanceV2LeaveRequest_.person), person);
        p = cb.and(p, cb.equal(root.get(AttendanceV2LeaveRequest_.leaveTypeId), leaveTypeId));
        p = cb.and(p, cb.lessThanOrEqualTo(root.get(AttendanceV2LeaveRequest_.startTime), endDate));
        p = cb.and(p, cb.greaterThanOrEqualTo(root.get(AttendanceV2LeaveRequest_.endTime), startDate));
        p = cb.and(p, cb.equal(root.get(AttendanceV2LeaveRequest_.status), LeaveRequestStatusEnum.APPLYING.getValue()));
        cq.select(cb.count(root)).where(p);
        return em.createQuery(cq).getSingleResult();
    }

    private YearRange currentYearRange() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        calendar.clear();
        calendar.set(year, Calendar.JANUARY, 1, 0, 0, 0);
        Date startDate = calendar.getTime();
        calendar.clear();
        calendar.set(year, Calendar.DECEMBER, 31, 23, 59, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        Date endDate = calendar.getTime();
        return new YearRange(year, startDate, endDate);
    }

    public static class Wi extends GsonPropertyObject {

        private static final long serialVersionUID = -8809321346325663056L;

        @FieldDescribe("人员标识")
        private String person;

        @FieldDescribe("假期类型ID")
        private String leaveTypeId;

        public String getPerson() {
            return person;
        }

        public void setPerson(String person) {
            this.person = person;
        }

        public String getLeaveTypeId() {
            return leaveTypeId;
        }

        public void setLeaveTypeId(String leaveTypeId) {
            this.leaveTypeId = leaveTypeId;
        }
    }

    public static class Wo extends GsonPropertyObject {

        private static final long serialVersionUID = 7216800226516451476L;

        @FieldDescribe("人员DN")
        private String person;

        @FieldDescribe("假期类型ID")
        private String leaveTypeId;

        @FieldDescribe("统计年份")
        private Integer year;

        @FieldDescribe("统计开始日期")
        private String startDate;

        @FieldDescribe("统计结束日期")
        private String endDate;

        @FieldDescribe("请假次数")
        private Long count;

        public String getPerson() {
            return person;
        }

        public void setPerson(String person) {
            this.person = person;
        }

        public String getLeaveTypeId() {
            return leaveTypeId;
        }

        public void setLeaveTypeId(String leaveTypeId) {
            this.leaveTypeId = leaveTypeId;
        }

        public Integer getYear() {
            return year;
        }

        public void setYear(Integer year) {
            this.year = year;
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

        public Long getCount() {
            return count;
        }

        public void setCount(Long count) {
            this.count = count;
        }
    }

    private static class YearRange {

        private final Integer year;
        private final Date startDate;
        private final Date endDate;

        private YearRange(Integer year, Date startDate, Date endDate) {
            this.year = year;
            this.startDate = startDate;
            this.endDate = endDate;
        }

        private Integer getYear() {
            return year;
        }

        private Date getStartDate() {
            return startDate;
        }

        private Date getEndDate() {
            return endDate;
        }
    }
}
