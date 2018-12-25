package com.x.processplatform.assemble.bam.factory;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.processplatform.assemble.bam.AbstractFactory;
import com.x.processplatform.assemble.bam.Business;
import com.x.processplatform.assemble.bam.stub.UnitStub;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.TaskCompleted_;

public class TaskCompletedFactory extends AbstractFactory {

	public TaskCompletedFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}



	public Long count(Date start, UnitStub unitStub) throws Exception {
		EntityManager em = this.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.greaterThan(root.get(TaskCompleted_.completedTime), start);
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.unit), unitStub.getValue()));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}



	public Long expiredCount(Date start, UnitStub unitStub) throws Exception {
		EntityManager em = this.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.greaterThan(root.get(TaskCompleted_.completedTime), start);
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.unit), unitStub.getValue()));
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.expired), true));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}



}