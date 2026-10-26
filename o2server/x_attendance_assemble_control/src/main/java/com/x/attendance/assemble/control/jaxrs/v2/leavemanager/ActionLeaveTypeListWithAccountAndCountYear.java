package com.x.attendance.assemble.control.jaxrs.v2.leavemanager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionNotExistObject;
import com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model.AttendanceV2LeaveRequestEnums.LeaveRequestStatusEnum;
import com.x.attendance.entity.v2.AttendanceV2LeaveAccount;
import com.x.attendance.entity.v2.AttendanceV2LeaveRequest;
import com.x.attendance.entity.v2.AttendanceV2LeaveRequest_;
import com.x.attendance.entity.v2.AttendanceV2LeaveType;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.tools.DateTools;

public class ActionLeaveTypeListWithAccountAndCountYear extends BaseAction {

    ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String person) throws Exception {
        ActionResult<List<Wo>> result = new ActionResult<>();
        List<AttendanceV2LeaveType> types = getLeaveTypeList(null);
        List<Wo> wos = types.stream().map(type -> Wo.copier.copy(type)).collect(Collectors.toList());

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            Person personObject = business.organization().person().getObject(person, true);
            if (personObject == null) {
                throw new ExceptionNotExistObject("人员 " + person);
            }
            List<AttendanceV2LeaveAccount> accounts = emc.listEqual(AttendanceV2LeaveAccount.class,
                    AttendanceV2LeaveAccount.person_FIELDNAME, personObject.getDistinguishedName());
            ActionLeaveTypeListWithAccount.attachAccounts(wos, accounts);

            YearRange yearRange = currentYearRange();
            Map<String, Statistic> statisticMap = statisticLeaveRequest(emc, personObject.getDistinguishedName(), yearRange.getStartDate(),
                    yearRange.getEndDate());
            attachRequestCountYear(wos, personObject.getDistinguishedName(), yearRange, statisticMap);
        }

        result.setData(wos);
        return result;
    }

    private void attachRequestCountYear(List<Wo> wos, String person, YearRange yearRange,
            Map<String, Statistic> statisticMap) {
        String startDate = DateTools.format(yearRange.getStartDate(), DateTools.format_yyyyMMdd);
        String endDate = DateTools.format(yearRange.getEndDate(), DateTools.format_yyyyMMdd);
        for (Wo wo : wos) {
            Statistic statistic = statisticMap.getOrDefault(wo.getId(), new Statistic(0L, 0.0));
            ActionLeaveRequestCountYear.Wo requestCountYear = new ActionLeaveRequestCountYear.Wo();
            requestCountYear.setPerson(person);
            requestCountYear.setLeaveTypeId(wo.getId());
            requestCountYear.setYear(yearRange.getYear());
            requestCountYear.setStartDate(startDate);
            requestCountYear.setEndDate(endDate);
            requestCountYear.setCount(statistic.getCount());
            requestCountYear.setTotalDays(statistic.getTotalDays());
            wo.setRequestCountYear(requestCountYear);
        }
    }

    private Map<String, Statistic> statisticLeaveRequest(EntityManagerContainer emc, String person, Date startDate,
            Date endDate) throws Exception {
        EntityManager em = emc.get(AttendanceV2LeaveRequest.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2LeaveRequest> cq = cb.createQuery(AttendanceV2LeaveRequest.class);
        Root<AttendanceV2LeaveRequest> root = cq.from(AttendanceV2LeaveRequest.class);
        Predicate p = cb.equal(root.get(AttendanceV2LeaveRequest_.person), person);
        p = cb.and(p, cb.lessThanOrEqualTo(root.get(AttendanceV2LeaveRequest_.startTime), endDate));
        p = cb.and(p, cb.greaterThanOrEqualTo(root.get(AttendanceV2LeaveRequest_.endTime), startDate));
        p = cb.and(p,
                cb.equal(root.get(AttendanceV2LeaveRequest_.status), LeaveRequestStatusEnum.APPLYING.getValue()));
        cq.select(root).where(p);
        List<AttendanceV2LeaveRequest> list = em.createQuery(cq).getResultList();
        if (list == null || list.isEmpty()) {
            return Collections.emptyMap();
        }
        return list.stream().filter(request -> StringUtils.isNotBlank(request.getLeaveTypeId()))
                .collect(Collectors.groupingBy(AttendanceV2LeaveRequest::getLeaveTypeId,
                        Collectors.collectingAndThen(Collectors.toList(), requests -> {
                            double totalDays = 0.0;
                            for (AttendanceV2LeaveRequest request : requests) {
                                totalDays += calculateDuration(request);
                            }
                            totalDays = BigDecimal.valueOf(totalDays).setScale(1, RoundingMode.HALF_UP).doubleValue();
                            return new Statistic((long) requests.size(), totalDays);
                        })));
    }

    private Double calculateDuration(AttendanceV2LeaveRequest request) {
        if (request.getDuration() != null) {
            return request.getDuration();
        }
        if (request.getStartTime() == null || request.getEndTime() == null
                || request.getEndTime().before(request.getStartTime())) {
            return 0.0;
        }
        long interval = request.getEndTime().getTime() - request.getStartTime().getTime();
        double days = interval / (1000.0 * 3600 * 24);
        return BigDecimal.valueOf(days).setScale(1, RoundingMode.HALF_UP).doubleValue();
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

    public static class Wo extends ActionLeaveTypeListWithAccount.Wo {

        private static final long serialVersionUID = 4467965829545819638L;

        static WrapCopier<AttendanceV2LeaveType, Wo> copier = WrapCopierFactory.wo(AttendanceV2LeaveType.class,
                Wo.class, null, JpaObject.FieldsInvisible);

        @FieldDescribe("当前用户当年请假统计")
        private ActionLeaveRequestCountYear.Wo requestCountYear;

        public ActionLeaveRequestCountYear.Wo getRequestCountYear() {
            return requestCountYear;
        }

        public void setRequestCountYear(ActionLeaveRequestCountYear.Wo requestCountYear) {
            this.requestCountYear = requestCountYear;
        }
    }

    private static class Statistic {

        private final Long count;
        private final Double totalDays;

        private Statistic(Long count, Double totalDays) {
            this.count = count;
            this.totalDays = totalDays;
        }

        private Long getCount() {
            return count;
        }

        private Double getTotalDays() {
            return totalDays;
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
