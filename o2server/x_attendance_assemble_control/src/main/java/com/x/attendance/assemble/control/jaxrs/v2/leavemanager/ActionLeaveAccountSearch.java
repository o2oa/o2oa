package com.x.attendance.assemble.control.jaxrs.v2.leavemanager;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.v2.AttendanceV2LeaveAccount;
import com.x.attendance.entity.v2.AttendanceV2LeaveAccount_;
import com.x.attendance.entity.v2.AttendanceV2LeaveType;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionLeaveAccountSearch extends BaseAction {

    private static final Logger logger = LoggerFactory.getLogger(ActionLeaveAccountSearch.class);

    ActionResult<Wo> execute(JsonElement jsonElement) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            if ((wi.getFilterList() == null || wi.getFilterList().isEmpty())) {
                logger.warn("查询条件不完整，查询条件：{0}", wi);
                return result;
            }
            List<String> userList = new ArrayList<>();
            Business business = new Business(emc);
            if (wi.getFilterList() != null && !wi.getFilterList().isEmpty()) {
                for (String f : wi.getFilterList()) {
                    analysisPerson(userList, f, business);
                }
            }
            if (userList.isEmpty()) {
                logger.warn("没有找到人员信息，查询条件：{0}", wi.getFilterList());
                return result;
            }
            // userList 去重
            java.util.Set<String> set = new java.util.HashSet<>(userList);
            List<String> distinctUserList = new ArrayList<>(set);
            List<AttendanceV2LeaveAccount> accounts = findAccountsWithPersonAndType(distinctUserList,
                    emc);

            List<AttendanceV2LeaveType> leaveTypeList = getLeaveTypeList(null);
            Wo wo = new Wo();
            wo.setAccountList(accounts);
            wo.setPersonList(distinctUserList);
            wo.setLeaveTypeList(leaveTypeList);
            result.setCount((long) accounts.size());
            result.setData(wo);
            return result;
        }
    }

    // 根据人员和假期类型查询余额账户 分页查询
    private List<AttendanceV2LeaveAccount> findAccountsWithPersonAndType(List<String> userList,
            EntityManagerContainer emc)
            throws Exception {
        EntityManager em = emc.get(AttendanceV2LeaveAccount.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2LeaveAccount> cq = cb.createQuery(AttendanceV2LeaveAccount.class);
        Root<AttendanceV2LeaveAccount> root = cq.from(AttendanceV2LeaveAccount.class);
        Predicate p = root.get(AttendanceV2LeaveAccount_.person).in(userList);
        cq.select(root).where(p).orderBy(cb.desc(root.get(AttendanceV2LeaveAccount_.updateTime)));
        return em.createQuery(cq).getResultList();
    }

    // 根据人员和假期类型查询余额账户数量
    private Long countAccountsWithPersonAndType(List<String> userList, List<String> leaveTypeList,
            EntityManagerContainer emc) throws Exception {
        EntityManager em = emc.get(AttendanceV2LeaveAccount.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<AttendanceV2LeaveAccount> root = cq.from(AttendanceV2LeaveAccount.class);
        Predicate p = root.get(AttendanceV2LeaveAccount_.person).in(userList);
        p = cb.and(p, root.get(AttendanceV2LeaveAccount_.leaveTypeId).in(leaveTypeList));
        cq.select(cb.count(root)).where(p);
        return em.createQuery(cq).getSingleResult();
    }

    // 根据假期类型ID列表查询假期类型列表
    private List<AttendanceV2LeaveType> getLeaveTypeList(List<String> idList,
            EntityManagerContainer emc)
            throws Exception {
        EntityManager em = emc.get(AttendanceV2LeaveType.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2LeaveType> cq = cb.createQuery(AttendanceV2LeaveType.class);
        Root<AttendanceV2LeaveType> root = cq.from(AttendanceV2LeaveType.class);
        Predicate p = root.get(JpaObject.id_FIELDNAME).in(idList);
        cq.select(root).where(p);
        return em.createQuery(cq).getResultList();
    }

    public static class Wi extends GsonPropertyObject {

        private static final long serialVersionUID = 5802792521103850683L;

        @FieldDescribe("过滤人员或组织，组织只支持单层: 用户或组织的DN，如xxx@xxx@P、xxx@xxx@U")
        private List<String> filterList;

//        @FieldDescribe("过滤的假期类型ID列表")
//        private List<String> leaveTypeList;

        public List<String> getFilterList() {
            return filterList;
        }

        public void setFilterList(List<String> filterList) {
            this.filterList = filterList;
        }
//
//        public List<String> getLeaveTypeList() {
//            return leaveTypeList;
//        }
//
//        public void setLeaveTypeList(List<String> leaveTypeList) {
//            this.leaveTypeList = leaveTypeList;
//        }

    }

    public static class Wo extends GsonPropertyObject {

        private static final long serialVersionUID = -987683927054761311L;

        @FieldDescribe("余额账户列表")
        private List<AttendanceV2LeaveAccount> accountList;
        @FieldDescribe("人员列表")
        private List<String> personList;
        @FieldDescribe("假期类型列表")
        private List<AttendanceV2LeaveType> leaveTypeList;

        public List<AttendanceV2LeaveAccount> getAccountList() {
            return accountList;
        }

        public void setAccountList(List<AttendanceV2LeaveAccount> accountList) {
            this.accountList = accountList;
        }

        public List<String> getPersonList() {
            return personList;
        }

        public void setPersonList(List<String> personList) {
            this.personList = personList;
        }

        public List<AttendanceV2LeaveType> getLeaveTypeList() {
            return leaveTypeList;
        }

        public void setLeaveTypeList(List<AttendanceV2LeaveType> leaveTypeList) {
            this.leaveTypeList = leaveTypeList;
        }

    }
}
