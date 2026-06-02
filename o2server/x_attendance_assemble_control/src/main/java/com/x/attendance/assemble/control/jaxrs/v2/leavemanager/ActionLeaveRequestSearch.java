package com.x.attendance.assemble.control.jaxrs.v2.leavemanager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.detail.ExceptionDateEndBeforeStartError;
import com.x.attendance.entity.v2.AttendanceV2LeaveRequest;
import com.x.attendance.entity.v2.AttendanceV2LeaveRequest_;
import com.x.attendance.entity.v2.AttendanceV2LeaveType;
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
import com.x.base.core.project.tools.DateTools;

public class ActionLeaveRequestSearch extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionLeaveRequestSearch.class);

    ActionResult<List<Wo>> execute(Integer page, Integer size, JsonElement jsonElement) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<List<Wo>> result = new ActionResult<>();
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            List<String> userList = new ArrayList<>();
            Business business = new Business(emc);
            if (wi.getFilterList() != null && !wi.getFilterList().isEmpty()) {
                for (String f : wi.getFilterList()) {
                    analysisPerson(userList, f, business);
                }
            }
            if (userList.isEmpty()) {
                logger.warn("没有找到人员信息，查询条件：{0}", wi.getFilterList());
                result.setData(new ArrayList<>());
                return result;
            }
            Date startDate = null;
            if (wi.getStartDate() != null && !wi.getStartDate().isEmpty()) {
                startDate = DateTools.parse(wi.getStartDate(), DateTools.format_yyyyMMdd); // 检查格式
            }
            Date endDate = null;
            if (wi.getEndDate() != null && !wi.getEndDate().isEmpty()) {
                endDate = DateTools.parse(wi.getEndDate(), DateTools.format_yyyyMMdd); // 检查格式
            }
            if (startDate != null && endDate != null && startDate.after(endDate)) {
                throw new ExceptionDateEndBeforeStartError();
            }
            Integer adjustPage = this.adjustPage(page);
            Integer adjustPageSize = this.adjustSize(size);
            List<AttendanceV2LeaveRequest> list = findRequestWithPersonAndTime(userList, startDate, endDate, emc, adjustPage, adjustPageSize);
            List<AttendanceV2LeaveType> leaveTypeList = getLeaveTypeList(null);
            List<Wo> wos = list.stream().map(request -> {
                Wo wo = Wo.copier.copy(request);
                // 设置假期类型
                for (AttendanceV2LeaveType type : leaveTypeList) {
                    if (type.getId().equals(request.getLeaveTypeId())) {
                        wo.setLeaveType(type);
                        break;
                    }
                }
                return wo;
            }).collect(Collectors.toList());
            result.setData(wos);
            result.setCount(countRequestWithPersonAndTime(userList, startDate, endDate, emc));
            return result;
        }
    }

    // 根据人员、时间查询请假申请 分页查询
    private List<AttendanceV2LeaveRequest> findRequestWithPersonAndTime(List<String> userList, Date startDate,
            Date endDate, EntityManagerContainer emc, Integer adjustPage, Integer adjustPageSize) throws Exception {
        EntityManager em = emc.get(AttendanceV2LeaveRequest.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2LeaveRequest> cq = cb.createQuery(AttendanceV2LeaveRequest.class);
        Root<AttendanceV2LeaveRequest> root = cq.from(AttendanceV2LeaveRequest.class);
        Predicate p = root.get(AttendanceV2LeaveRequest_.person).in(userList);
        if (endDate != null) {
           p = cb.and(p, cb.lessThanOrEqualTo(root.get(AttendanceV2LeaveRequest_.startTime), endDate));
        }
        if (startDate != null) {
            p = cb.and(p, cb.greaterThanOrEqualTo(root.get(AttendanceV2LeaveRequest_.endTime), startDate));
        }
        cq.select(root).where(p).orderBy(cb.desc(root.get(AttendanceV2LeaveRequest_.createTime)));
        return em.createQuery(cq).setFirstResult((adjustPage - 1) * adjustPageSize).setMaxResults(adjustPageSize).getResultList();
    }
    // 根据人员、时间查询请假申请数量
    private Long countRequestWithPersonAndTime(List<String> userList, Date startDate,
            Date endDate, EntityManagerContainer emc) throws Exception {
        EntityManager em = emc.get(AttendanceV2LeaveRequest.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<AttendanceV2LeaveRequest> root = cq.from(AttendanceV2LeaveRequest.class);
        Predicate p = root.get(AttendanceV2LeaveRequest_.person).in(userList);
        if (endDate != null) {
            p = cb.and(p, cb.lessThanOrEqualTo(root.get(AttendanceV2LeaveRequest_.startTime), endDate));
        }
        if (startDate != null) {
            p = cb.and(p, cb.greaterThanOrEqualTo(root.get(AttendanceV2LeaveRequest_.endTime), startDate));
        }
        cq.select(cb.count(root)).where(p);
        return em.createQuery(cq).getSingleResult();
    }

    public static class Wi extends GsonPropertyObject {
        private static final long serialVersionUID = 8433642169523374771L;

        @FieldDescribe("过滤人员或组织，组织只支持单层: 用户或组织的DN，如xxx@xxx@P、xxx@xxx@U")
        private List<String> filterList;

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

    public static class Wo extends AttendanceV2LeaveRequest {

        private static final long serialVersionUID = 5750870182729872170L;

        static WrapCopier<AttendanceV2LeaveRequest, Wo> copier = WrapCopierFactory.wo(AttendanceV2LeaveRequest.class,
                Wo.class, null,
                JpaObject.FieldsInvisible);

        private AttendanceV2LeaveType leaveType;

        public AttendanceV2LeaveType getLeaveType() {
            return leaveType;
        }

        public void setLeaveType(AttendanceV2LeaveType leaveType) {
            this.leaveType = leaveType;
        }

    }

}
