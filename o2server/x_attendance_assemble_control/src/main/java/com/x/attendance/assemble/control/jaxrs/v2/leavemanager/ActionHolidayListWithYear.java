package com.x.attendance.assemble.control.jaxrs.v2.leavemanager;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.attendance.assemble.control.jaxrs.v2.ExceptionWithMessage;
import com.x.attendance.entity.v2.AttendanceV2Holiday;
import com.x.attendance.entity.v2.AttendanceV2Holiday_;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;

public class ActionHolidayListWithYear extends BaseAction {

    ActionResult<Wo> execute(Integer year) throws Exception {
        if (year == null) {
            throw new ExceptionWithMessage("年份不能为空");
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            List<AttendanceV2Holiday> holidays = listWithYear(emc, year);
            Wo wo = new Wo();
            wo.setYear(year);
            wo.setWorkdayList(new ArrayList<>());
            wo.setOffdayList(new ArrayList<>());
            for (AttendanceV2Holiday holiday : holidays) {
                if (Boolean.TRUE.equals(holiday.getOffDay())) {
                    wo.getOffdayList().add(holiday);
                } else {
                    wo.getWorkdayList().add(holiday);
                }
            }
            result.setCount((long) holidays.size());
            result.setData(wo);
            return result;
        }
    }

    private List<AttendanceV2Holiday> listWithYear(EntityManagerContainer emc, Integer year) throws Exception {
        EntityManager em = emc.get(AttendanceV2Holiday.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2Holiday> cq = cb.createQuery(AttendanceV2Holiday.class);
        Root<AttendanceV2Holiday> root = cq.from(AttendanceV2Holiday.class);
        Predicate p = cb.equal(root.get(AttendanceV2Holiday_.year), year);
        cq.select(root).where(p).orderBy(cb.asc(root.get(AttendanceV2Holiday_.dateString)));
        return em.createQuery(cq).getResultList();
    }

    public static class Wo extends GsonPropertyObject {

        private static final long serialVersionUID = 8927136890101437265L;

        @FieldDescribe("年份")
        private Integer year;

        @FieldDescribe("工作日数组")
        private List<AttendanceV2Holiday> workdayList;

        @FieldDescribe("放假日数组")
        private List<AttendanceV2Holiday> offdayList;

        public Integer getYear() {
            return year;
        }

        public void setYear(Integer year) {
            this.year = year;
        }

        public List<AttendanceV2Holiday> getWorkdayList() {
            return workdayList;
        }

        public void setWorkdayList(List<AttendanceV2Holiday> workdayList) {
            this.workdayList = workdayList;
        }

        public List<AttendanceV2Holiday> getOffdayList() {
            return offdayList;
        }

        public void setOffdayList(List<AttendanceV2Holiday> offdayList) {
            this.offdayList = offdayList;
        }
    }
}
