package com.x.attendance.assemble.control.jaxrs.v2.leavemanager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionWithMessage;
import com.x.attendance.entity.v2.AttendanceV2LeaveTransaction;
import com.x.attendance.entity.v2.AttendanceV2LeaveTransaction_;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;

public class ActionLeaveTransactionSearch extends BaseAction {

    ActionResult<Wo> execute(JsonElement jsonElement) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            List<String> filterList = filterBlank(wi.getFilterList());
            List<String> leaveTypeList = filterBlank(wi.getLeaveTypeList());
            if (filterList.isEmpty() && leaveTypeList.isEmpty()) {
                throw new ExceptionWithMessage("用户和假期类型至少需要一个查询条件");
            }
            List<String> personList = listPerson(filterList, emc);
            if (!filterList.isEmpty() && personList.isEmpty()) {
                result.setCount(0L);
                result.setData(new Wo());
                return result;
            }
            List<AttendanceV2LeaveTransaction> transactionList = findTransactions(personList, leaveTypeList, emc);
            Wo wo = new Wo();
            wo.setPersonList(personList);
            wo.setLeaveTypeList(leaveTypeList);
            wo.setTransactionList(transactionList);
            result.setCount((long) transactionList.size());
            result.setData(wo);
            return result;
        }
    }

    private List<String> listPerson(List<String> filterList, EntityManagerContainer emc) throws Exception {
        List<String> personList = new ArrayList<>();
        if (!filterList.isEmpty()) {
            Business business = new Business(emc);
            for (String filter : filterList) {
                analysisPerson(personList, filter, business);
            }
        }
        return personList;
    }

    private List<AttendanceV2LeaveTransaction> findTransactions(List<String> personList, List<String> leaveTypeList,
            EntityManagerContainer emc) throws Exception {
        EntityManager em = emc.get(AttendanceV2LeaveTransaction.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2LeaveTransaction> cq = cb.createQuery(AttendanceV2LeaveTransaction.class);
        Root<AttendanceV2LeaveTransaction> root = cq.from(AttendanceV2LeaveTransaction.class);
        Predicate p = null;
        if (!personList.isEmpty()) {
            p = root.get(AttendanceV2LeaveTransaction_.person).in(personList);
        }
        if (!leaveTypeList.isEmpty()) {
            Predicate leaveTypePredicate = root.get(AttendanceV2LeaveTransaction_.leaveTypeId).in(leaveTypeList);
            p = p == null ? leaveTypePredicate : cb.and(p, leaveTypePredicate);
        }
        cq.select(root).where(p).orderBy(cb.desc(root.<Date>get(JpaObject.updateTime_FIELDNAME)));
        return em.createQuery(cq).getResultList();
    }

    private List<String> filterBlank(List<String> list) {
        List<String> result = new ArrayList<>();
        if (list != null) {
            for (String item : list) {
                if (StringUtils.isNotBlank(item)) {
                    result.add(item);
                }
            }
        }
        return result;
    }

    public static class Wi extends GsonPropertyObject {

        private static final long serialVersionUID = -3766728976122646384L;

        @FieldDescribe("过滤人员或组织，组织只支持单层: 用户或组织的DN，如xxx@xxx@P、xxx@xxx@U")
        private List<String> filterList;

        @FieldDescribe("过滤的假期类型ID列表")
        private List<String> leaveTypeList;

        public List<String> getFilterList() {
            return filterList;
        }

        public void setFilterList(List<String> filterList) {
            this.filterList = filterList;
        }

        public List<String> getLeaveTypeList() {
            return leaveTypeList;
        }

        public void setLeaveTypeList(List<String> leaveTypeList) {
            this.leaveTypeList = leaveTypeList;
        }
    }

    public static class Wo extends GsonPropertyObject {

        private static final long serialVersionUID = -7462344787658355916L;

        @FieldDescribe("假期流水列表")
        private List<AttendanceV2LeaveTransaction> transactionList;

        @FieldDescribe("人员列表")
        private List<String> personList;

        @FieldDescribe("假期类型ID列表")
        private List<String> leaveTypeList;

        public List<AttendanceV2LeaveTransaction> getTransactionList() {
            return transactionList;
        }

        public void setTransactionList(List<AttendanceV2LeaveTransaction> transactionList) {
            this.transactionList = transactionList;
        }

        public List<String> getPersonList() {
            return personList;
        }

        public void setPersonList(List<String> personList) {
            this.personList = personList;
        }

        public List<String> getLeaveTypeList() {
            return leaveTypeList;
        }

        public void setLeaveTypeList(List<String> leaveTypeList) {
            this.leaveTypeList = leaveTypeList;
        }
    }
}
