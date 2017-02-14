package com.x.processplatform.assemble.bam.factory;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.processplatform.assemble.bam.AbstractFactory;
import com.x.processplatform.assemble.bam.Business;
import com.x.processplatform.assemble.bam.stub.ActivityStub;
import com.x.processplatform.assemble.bam.stub.ApplicationStub;
import com.x.processplatform.assemble.bam.stub.CompanyStub;
import com.x.processplatform.assemble.bam.stub.DepartmentStub;
import com.x.processplatform.assemble.bam.stub.PersonStub;
import com.x.processplatform.assemble.bam.stub.ProcessStub;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.TaskCompleted_;

public class TaskCompletedFactory extends AbstractFactory {

	public TaskCompletedFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	public Long count(Date start, ApplicationStub applicationStub) throws Exception {
		EntityManager em = this.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.greaterThan(root.get(TaskCompleted_.startTime), start);
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.application), applicationStub.getValue()));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public Long count(Date start, ProcessStub processStub) throws Exception {
		EntityManager em = this.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.greaterThan(root.get(TaskCompleted_.startTime), start);
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.process), processStub.getValue()));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public Long count(Date start, ActivityStub activityStub) throws Exception {
		EntityManager em = this.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.greaterThan(root.get(TaskCompleted_.startTime), start);
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.activity), activityStub.getValue()));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public Long count(Date start, CompanyStub companyStub) throws Exception {
		EntityManager em = this.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.greaterThan(root.get(TaskCompleted_.completedTime), start);
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.company), companyStub.getValue()));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public Long count(Date start, DepartmentStub departmentStub) throws Exception {
		EntityManager em = this.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.greaterThan(root.get(TaskCompleted_.completedTime), start);
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.department), departmentStub.getValue()));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public Long count(Date start, PersonStub personStub) throws Exception {
		EntityManager em = this.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.greaterThan(root.get(TaskCompleted_.completedTime), start);
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.person), personStub.getValue()));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public Long expiredCount(Date start, ApplicationStub applicationStub) throws Exception {
		EntityManager em = this.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.greaterThan(root.get(TaskCompleted_.startTime), start);
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.expired), true));
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.application), applicationStub.getValue()));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public Long expiredCount(Date start, ProcessStub processStub) throws Exception {
		EntityManager em = this.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.greaterThan(root.get(TaskCompleted_.startTime), start);
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.expired), true));
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.process), processStub.getValue()));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public Long expiredCount(Date start, ActivityStub activityStub) throws Exception {
		EntityManager em = this.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.greaterThan(root.get(TaskCompleted_.startTime), start);
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.expired), true));
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.activity), activityStub.getValue()));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public Long expiredCount(Date start, CompanyStub companyStub) throws Exception {
		EntityManager em = this.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.greaterThan(root.get(TaskCompleted_.completedTime), start);
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.company), companyStub.getValue()));
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.expired), true));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public Long expiredCount(Date start, DepartmentStub departmentStub) throws Exception {
		EntityManager em = this.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.greaterThan(root.get(TaskCompleted_.completedTime), start);
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.department), departmentStub.getValue()));
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.expired), true));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public Long expiredCount(Date start, PersonStub personStub) throws Exception {
		EntityManager em = this.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.greaterThan(root.get(TaskCompleted_.completedTime), start);
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.person), personStub.getValue()));
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.expired), true));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public Long duration(Date start, ApplicationStub applicationStub) throws Exception {
		EntityManager em = this.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.greaterThan(root.get(TaskCompleted_.startTime), start);
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.application), applicationStub.getValue()));
		cq.select(cb.sum(root.get(TaskCompleted_.duration))).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public Long duration(Date start, ProcessStub processStub) throws Exception {
		EntityManager em = this.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.greaterThan(root.get(TaskCompleted_.startTime), start);
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.process), processStub.getValue()));
		cq.select(cb.sum(root.get(TaskCompleted_.duration))).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public Long duration(Date start, ActivityStub activityStub) throws Exception {
		EntityManager em = this.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.greaterThan(root.get(TaskCompleted_.startTime), start);
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.activity), activityStub.getValue()));
		cq.select(cb.sum(root.get(TaskCompleted_.duration))).where(p);
		return em.createQuery(cq).getSingleResult();
	}

}