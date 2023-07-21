package com.x.processplatform.service.processing.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.Task_;
import com.x.processplatform.service.processing.AbstractFactory;
import com.x.processplatform.service.processing.Business;

public class TaskFactory extends AbstractFactory {

    public TaskFactory(Business business) throws Exception {
        super(business);
    }

    public List<String> listWithWork(String id) throws Exception {
        EntityManager em = this.entityManagerContainer().get(Task.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> cq = cb.createQuery(String.class);
        Root<Task> root = cq.from(Task.class);
        Predicate p = cb.equal(root.get(Task_.work), id);
        cq.select(root.get(Task_.id)).where(p);
        return em.createQuery(cq).getResultList();
    }

}