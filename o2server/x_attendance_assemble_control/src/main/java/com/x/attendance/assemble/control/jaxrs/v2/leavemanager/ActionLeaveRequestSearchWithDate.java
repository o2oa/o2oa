package com.x.attendance.assemble.control.jaxrs.v2.leavemanager;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.control.jaxrs.v2.ExceptionWithMessage;
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
import com.x.base.core.project.tools.DateTools;

public class ActionLeaveRequestSearchWithDate extends BaseAction {

    ActionResult<List<Wo>> execute(String dateString) throws Exception {
        Date date = parseDate(dateString);
        Date nextDate = DateTools.addDay(date, 1);
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<List<Wo>> result = new ActionResult<>();
            List<AttendanceV2LeaveRequest> list = findRequestWithDate(date, nextDate, emc);
            List<AttendanceV2LeaveType> leaveTypeList = getLeaveTypeList(null);
            List<Wo> wos = list.stream().map(request -> {
                Wo wo = Wo.copier.copy(request);
                for (AttendanceV2LeaveType type : leaveTypeList) {
                    if (type.getId().equals(request.getLeaveTypeId())) {
                        wo.setLeaveType(type);
                        break;
                    }
                }
                return wo;
            }).collect(Collectors.toList());
            result.setData(wos);
            result.setCount((long) wos.size());
            return result;
        }
    }

    private Date parseDate(String dateString) throws Exception {
        if (StringUtils.isBlank(dateString) || DateTools.parseDate(dateString) == null) {
            throw new ExceptionWithMessage("日期不能为空，格式为 yyyy-MM-dd");
        }
        return DateTools.parse(dateString, DateTools.format_yyyyMMdd);
    }

    // 查询和当天 [date, nextDate) 有交集的请假申请。
    private List<AttendanceV2LeaveRequest> findRequestWithDate(Date date, Date nextDate, EntityManagerContainer emc)
            throws Exception {
        EntityManager em = emc.get(AttendanceV2LeaveRequest.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2LeaveRequest> cq = cb.createQuery(AttendanceV2LeaveRequest.class);
        Root<AttendanceV2LeaveRequest> root = cq.from(AttendanceV2LeaveRequest.class);
        Predicate p = cb.lessThan(root.get(AttendanceV2LeaveRequest_.startTime), nextDate);
        p = cb.and(p, cb.greaterThanOrEqualTo(root.get(AttendanceV2LeaveRequest_.endTime), date));
        cq.select(root).where(p).orderBy(cb.asc(root.get(AttendanceV2LeaveRequest_.startTime)));
        return em.createQuery(cq).getResultList();
    }

    public static class Wo extends AttendanceV2LeaveRequest {

        private static final long serialVersionUID = 8515612353994855877L;

        static WrapCopier<AttendanceV2LeaveRequest, Wo> copier = WrapCopierFactory.wo(AttendanceV2LeaveRequest.class,
                Wo.class, null, JpaObject.FieldsInvisible);

        @FieldDescribe("假期类型")
        private AttendanceV2LeaveType leaveType;

        public AttendanceV2LeaveType getLeaveType() {
            return leaveType;
        }

        public void setLeaveType(AttendanceV2LeaveType leaveType) {
            this.leaveType = leaveType;
        }
    }
}
