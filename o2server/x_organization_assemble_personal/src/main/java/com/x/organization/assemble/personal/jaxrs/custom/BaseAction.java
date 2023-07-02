package com.x.organization.assemble.personal.jaxrs.custom;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.organization.core.entity.Custom;
import com.x.organization.core.entity.Custom_;

abstract class BaseAction extends StandardJaxrsAction {

    /**
     * 根据那么和人员查找
     * 
     * @param emc
     * @param person
     * @param name
     * @return
     * @throws Exception
     */
    Custom getWithName(EntityManagerContainer emc, String person, String name) throws Exception {
        EntityManager em = emc.get(Custom.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Custom> cq = cb.createQuery(Custom.class);
        Root<Custom> root = cq.from(Custom.class);
        Predicate p = cb.equal(root.get(Custom_.person), person);
        p = cb.and(p, cb.equal(root.get(Custom_.name), name));
        List<Custom> list = em.createQuery(cq.where(p)).setMaxResults(1).getResultList();
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }
}
