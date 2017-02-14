package com.x.processplatform.assemble.bam.factory;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.processplatform.assemble.bam.AbstractFactory;
import com.x.processplatform.assemble.bam.Business;
import com.x.processplatform.assemble.bam.stub.ApplicationStub;
import com.x.processplatform.assemble.bam.stub.ProcessStub;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkCompleted_;

public class WorkCompletedFactory extends AbstractFactory {

	public WorkCompletedFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	public Long count(Date start, ApplicationStub applicationStub) throws Exception {
		EntityManager em = this.entityManagerContainer().get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		Predicate p = cb.greaterThan(root.get(WorkCompleted_.startTime), start);
		p = cb.and(p, cb.equal(root.get(WorkCompleted_.application), applicationStub.getValue()));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public Long count(Date start, ProcessStub processStub) throws Exception {
		EntityManager em = this.entityManagerContainer().get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		Predicate p = cb.greaterThan(root.get(WorkCompleted_.startTime), start);
		p = cb.and(p, cb.equal(root.get(WorkCompleted_.process), processStub.getValue()));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public Long expiredCount(Date start, ApplicationStub applicationStub) throws Exception {
		EntityManager em = this.entityManagerContainer().get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		Predicate p = cb.greaterThan(root.get(WorkCompleted_.startTime), start);
		p = cb.and(p, cb.equal(root.get(WorkCompleted_.expired), true));
		p = cb.and(p, cb.equal(root.get(WorkCompleted_.application), applicationStub.getValue()));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public Long expiredCount(Date start, ProcessStub processStub) throws Exception {
		EntityManager em = this.entityManagerContainer().get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		Predicate p = cb.greaterThan(root.get(WorkCompleted_.startTime), start);
		p = cb.and(p, cb.equal(root.get(WorkCompleted_.expired), true));
		p = cb.and(p, cb.equal(root.get(WorkCompleted_.process), processStub.getValue()));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public Long duration(Date start, ApplicationStub applicationStub) throws Exception {
		EntityManager em = this.entityManagerContainer().get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		Predicate p = cb.greaterThan(root.get(WorkCompleted_.startTime), start);
		p = cb.and(p, cb.equal(root.get(WorkCompleted_.application), applicationStub.getValue()));
		cq.select(cb.sum(root.get(WorkCompleted_.duration))).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public Long duration(Date start, ProcessStub processStub) throws Exception {
		EntityManager em = this.entityManagerContainer().get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		Predicate p = cb.greaterThan(root.get(WorkCompleted_.startTime), start);
		p = cb.and(p, cb.equal(root.get(WorkCompleted_.process), processStub.getValue()));
		cq.select(cb.sum(root.get(WorkCompleted_.duration))).where(p);
		return em.createQuery(cq).getSingleResult();
	}

}