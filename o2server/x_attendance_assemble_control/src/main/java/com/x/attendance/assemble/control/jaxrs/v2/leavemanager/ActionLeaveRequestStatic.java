package com.x.attendance.assemble.control.jaxrs.v2.leavemanager;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.AttendanceV2Helper;
import com.x.attendance.assemble.control.jaxrs.v2.detail.ExceptionDateEndBeforeStartError;
import com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model.AttendanceV2LeaveRequestEnums.LeaveRequestStatusEnum;
import com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model.AttendanceV2LeaveTypeEnums.QuotaTypeEnum;
import com.x.attendance.entity.v2.AttendanceV2LeaveAccount;
import com.x.attendance.entity.v2.AttendanceV2LeaveAccount_;
import com.x.attendance.entity.v2.AttendanceV2LeaveRequest;
import com.x.attendance.entity.v2.AttendanceV2LeaveRequest_;
import com.x.attendance.entity.v2.AttendanceV2LeaveType;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.DateTools;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

public class ActionLeaveRequestStatic extends BaseAction {



    public ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<List<Wo>> result = new ActionResult<List<Wo>>();
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            List<String> personList = new ArrayList<>();
            Business business = new Business(emc);
            if (wi.getFilterList() != null && !wi.getFilterList().isEmpty()) {
                for (String filter : wi.getFilterList()) {
                    AttendanceV2Helper.analysisFilterToPersonList(personList, filter, business, wi.getRecursive());
                }
            }
            if (personList.isEmpty()) {
                result.setData(new ArrayList<>());
                result.setCount(0L);
                return result;
            }
            personList = distinct(personList);
            Date startDate = null;
            if (StringUtils.isNotBlank(wi.getStartDate())) {
                startDate = DateTools.parse(wi.getStartDate(), DateTools.format_yyyyMMdd);
            }
            Date endDate = null;
            if (StringUtils.isNotBlank(wi.getEndDate())) {
                endDate = DateTools.parse(wi.getEndDate(), DateTools.format_yyyyMMdd);
            }
            if (startDate != null && endDate != null && startDate.after(endDate)) {
                throw new ExceptionDateEndBeforeStartError();
            }
            Date endExclusiveDate = endDate == null ? null : DateTools.addDay(endDate, 1);

            List<AttendanceV2LeaveType> leaveTypeList = getLeaveTypeList(null);
            List<AttendanceV2LeaveRequest> requestList = findRequestWithPersonAndTime(personList, startDate,
                    endExclusiveDate, emc);
            List<AttendanceV2LeaveAccount> accountList = findAccountWithPerson(personList, emc);
            Map<String, Map<String, List<AttendanceV2LeaveRequest>>> requestMap = requestList.stream()
                    .filter(request -> StringUtils.isNotBlank(request.getPerson()))
                    .filter(request -> StringUtils.isNotBlank(request.getLeaveTypeId()))
                    .collect(Collectors.groupingBy(AttendanceV2LeaveRequest::getPerson,
                            Collectors.groupingBy(AttendanceV2LeaveRequest::getLeaveTypeId)));
            Map<String, Map<String, AttendanceV2LeaveAccount>> accountMap = accountList.stream()
                    .filter(account -> StringUtils.isNotBlank(account.getPerson()))
                    .filter(account -> StringUtils.isNotBlank(account.getLeaveTypeId()))
                    .collect(Collectors.groupingBy(AttendanceV2LeaveAccount::getPerson,
                            Collectors.toMap(AttendanceV2LeaveAccount::getLeaveTypeId, account -> account,
                                    (first, second) -> first)));

            List<Wo> woList = new ArrayList<>();
            for (String person : personList) {
                Wo wo = new Wo();
                wo.setPerson(person);
                wo.setLeaveRequestStaticList(buildRequestStaticList(leaveTypeList,
                        requestMap.getOrDefault(person, Collections.emptyMap())));
                wo.setLeaveTypeAccountList(buildLeaveTypeAccountList(leaveTypeList,
                        accountMap.getOrDefault(person, Collections.emptyMap())));
                woList.add(wo);
            }

            result.setData(woList);
            result.setCount((long) woList.size());
            return result;
        }
    }

    private List<String> distinct(List<String> personList) {
        Set<String> set = new HashSet<>();
        List<String> list = new ArrayList<>();
        for (String person : personList) {
            if (set.add(person)) {
                list.add(person);
            }
        }
        return list;
    }

    private List<WoLeaveRequestStatic> buildRequestStaticList(List<AttendanceV2LeaveType> leaveTypeList,
            Map<String, List<AttendanceV2LeaveRequest>> requestMap) {
        List<WoLeaveRequestStatic> list = new ArrayList<>();
        for (AttendanceV2LeaveType leaveType : leaveTypeList) {
            List<AttendanceV2LeaveRequest> requests = requestMap.getOrDefault(leaveType.getId(),
                    Collections.emptyList());
            WoLeaveRequestStatic wo = new WoLeaveRequestStatic();
            wo.setLeaveType(leaveType);
            wo.setLeaveRequestList(requests);
            wo.setTotalDays(calculateTotalDays(requests));
            list.add(wo);
        }
        return list;
    }

    private List<WoLeaveTypeAccount> buildLeaveTypeAccountList(List<AttendanceV2LeaveType> leaveTypeList,
            Map<String, AttendanceV2LeaveAccount> accountMap) {
        List<WoLeaveTypeAccount> list = new ArrayList<>();
        for (AttendanceV2LeaveType leaveType : leaveTypeList) {
            if (!QuotaTypeEnum.QUOTA.getValue().equals(leaveType.getQuotaType())) {
                continue;
            }
            AttendanceV2LeaveAccount account = accountMap.get(leaveType.getId());
            WoLeaveTypeAccount wo = new WoLeaveTypeAccount();
            wo.setLeaveType(leaveType);
            wo.setLeaveAccount(account);
            wo.setBalance(account == null || account.getBalance() == null ? 0.0 : account.getBalance());
            list.add(wo);
        }
        return list;
    }

    private List<AttendanceV2LeaveRequest> findRequestWithPersonAndTime(List<String> personList, Date startDate,
            Date endExclusiveDate, EntityManagerContainer emc) throws Exception {
        EntityManager em = emc.get(AttendanceV2LeaveRequest.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2LeaveRequest> cq = cb.createQuery(AttendanceV2LeaveRequest.class);
        Root<AttendanceV2LeaveRequest> root = cq.from(AttendanceV2LeaveRequest.class);
        Predicate p = root.get(AttendanceV2LeaveRequest_.person).in(personList);
        if (endExclusiveDate != null) {
            p = cb.and(p, cb.lessThan(root.get(AttendanceV2LeaveRequest_.startTime), endExclusiveDate));
        }
        if (startDate != null) {
            p = cb.and(p, cb.greaterThanOrEqualTo(root.get(AttendanceV2LeaveRequest_.endTime), startDate));
        }
        p = cb.and(p, cb.equal(root.get(AttendanceV2LeaveRequest_.status), LeaveRequestStatusEnum.APPLYING.getValue()));
        cq.select(root).where(p).orderBy(cb.asc(root.get(AttendanceV2LeaveRequest_.startTime)));
        return em.createQuery(cq).getResultList();
    }

    private List<AttendanceV2LeaveAccount> findAccountWithPerson(List<String> personList, EntityManagerContainer emc)
            throws Exception {
        EntityManager em = emc.get(AttendanceV2LeaveAccount.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2LeaveAccount> cq = cb.createQuery(AttendanceV2LeaveAccount.class);
        Root<AttendanceV2LeaveAccount> root = cq.from(AttendanceV2LeaveAccount.class);
        Predicate p = root.get(AttendanceV2LeaveAccount_.person).in(personList);
        cq.select(root).where(p).orderBy(cb.desc(root.get(AttendanceV2LeaveAccount_.updateTime)));
        return em.createQuery(cq).getResultList();
    }

    private Double calculateTotalDays(List<AttendanceV2LeaveRequest> requests) {
        double totalDays = 0.0;
        for (AttendanceV2LeaveRequest request : requests) {
            totalDays += calculateDuration(request);
        }
        return BigDecimal.valueOf(totalDays).setScale(1, RoundingMode.HALF_UP).doubleValue();
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



    public static class Wo extends GsonPropertyObject {

        @FieldDescribe("人员标识")
        private String person;

        @FieldDescribe("请假统计列表，全部假期类型")
        private List<WoLeaveRequestStatic> leaveRequestStaticList;

        @FieldDescribe("请假类型余额列表，有限额假期类型")
        private List<WoLeaveTypeAccount> leaveTypeAccountList;

        public String getPerson() {
            return person;
        }

        public void setPerson(String person) {
            this.person = person;
        }

        public List<WoLeaveRequestStatic> getLeaveRequestStaticList() {
            return leaveRequestStaticList;
        }

        public void setLeaveRequestStaticList(
                List<WoLeaveRequestStatic> leaveRequestStaticList) {
            this.leaveRequestStaticList = leaveRequestStaticList;
        }

        public List<WoLeaveTypeAccount> getLeaveTypeAccountList() {
            return leaveTypeAccountList;
        }

        public void setLeaveTypeAccountList(
                List<WoLeaveTypeAccount> leaveTypeAccountList) {
            this.leaveTypeAccountList = leaveTypeAccountList;
        }
    }

    public static class WoLeaveTypeAccount extends GsonPropertyObject {

        @FieldDescribe("请假类型")
        private AttendanceV2LeaveType leaveType;

        @FieldDescribe("余额账户")
        private AttendanceV2LeaveAccount leaveAccount;
        @FieldDescribe("余额")
        private Double balance;


        public Double getBalance() {
            return balance;
        }

        public void setBalance(Double balance) {
            this.balance = balance;
        }

        public AttendanceV2LeaveType getLeaveType() {
            return leaveType;
        }

        public void setLeaveType(AttendanceV2LeaveType leaveType) {
            this.leaveType = leaveType;
        }

        public AttendanceV2LeaveAccount getLeaveAccount() {
            return leaveAccount;
        }

        public void setLeaveAccount(AttendanceV2LeaveAccount leaveAccount) {
            this.leaveAccount = leaveAccount;
        }
    }

    public static class WoLeaveRequestStatic extends GsonPropertyObject {

        @FieldDescribe("请假类型")
        private AttendanceV2LeaveType leaveType;

        @FieldDescribe("对应请假类型的请假申请列表")
        private List<AttendanceV2LeaveRequest> leaveRequestList;

        @FieldDescribe("请假总天数")
        private Double totalDays;


        public AttendanceV2LeaveType getLeaveType() {
            return leaveType;
        }

        public void setLeaveType(AttendanceV2LeaveType leaveType) {
            this.leaveType = leaveType;
        }

        public List<AttendanceV2LeaveRequest> getLeaveRequestList() {
            return leaveRequestList;
        }

        public void setLeaveRequestList(
                List<AttendanceV2LeaveRequest> leaveRequestList) {
            this.leaveRequestList = leaveRequestList;
        }

        public Double getTotalDays() {
            return totalDays;
        }

        public void setTotalDays(Double totalDays) {
            this.totalDays = totalDays;
        }
    }

    public static class Wi extends GsonPropertyObject {
        @FieldDescribe("过滤人员或组织，组织默认递归: 用户或组织的DN，如xxx@xxx@P、xxx@xxx@U")
        private List<String> filterList;

        @FieldDescribe("过滤组织是否递归查询下级组织人员，默认true，false时仅查询当前组织直属人员")
        private Boolean recursive;

        @FieldDescribe("开始日期")
        private String startDate;

        @FieldDescribe("结束日期")
        private String endDate;


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
}
