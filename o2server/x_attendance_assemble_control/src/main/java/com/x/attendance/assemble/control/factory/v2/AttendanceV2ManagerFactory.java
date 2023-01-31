package com.x.attendance.assemble.control.factory.v2;

import com.x.attendance.assemble.control.AbstractFactory;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.v2.AttendanceV2Shift;
import com.x.attendance.entity.v2.AttendanceV2Shift_;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * Created by fancyLou on 2023/1/31.
 * Copyright © 2023 O2. All rights reserved.
 */
public class AttendanceV2ManagerFactory  extends AbstractFactory {

    public AttendanceV2ManagerFactory(Business business) throws Exception {
        super(business);
    }

    /**
     * 查询班次列表
     * 分页查询需要
     * @param adjustPage
     * @param adjustPageSize
     * @param name 可以为空
     * @return
     * @throws Exception
     */
    public List<AttendanceV2Shift> listShiftWithNameByPage(Integer adjustPage,
                                                   Integer adjustPageSize, String name) throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceV2Shift.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2Shift> cq = cb.createQuery(AttendanceV2Shift.class);
        Root<AttendanceV2Shift> root = cq.from(AttendanceV2Shift.class);
        if (StringUtils.isNotEmpty(name)) {
            Predicate p = cb.like(root.get(AttendanceV2Shift_.shiftName), "%" + name + "%");
            cq.select(root).where(p).orderBy(cb.desc(root.get(AttendanceV2Shift_.createTime)));
        } else {
            cq.select(root).orderBy(cb.desc(root.get(AttendanceV2Shift_.createTime)));
        }
        return em.createQuery(cq).setFirstResult((adjustPage - 1) * adjustPageSize).setMaxResults(adjustPageSize)
                .getResultList();
    }

    /**
     * 查询班次总数
     * 分页查询需要
     * @param name 可以为空
     * @return
     * @throws Exception
     */
    public Long shiftCountWithName(String name) throws Exception {
        EntityManager em = this.entityManagerContainer().get(AttendanceV2Shift.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<AttendanceV2Shift> root = cq.from(AttendanceV2Shift.class);
        if (StringUtils.isNotEmpty(name)) {
            Predicate p = cb.like(root.get(AttendanceV2Shift_.shiftName), "%" + name + "%");
            return em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult();
        }
        return em.createQuery(cq.select(cb.count(root))).getSingleResult();
    }
}
