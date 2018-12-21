package com.x.processplatform.assemble.bam.jaxrs.state;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.processplatform.assemble.bam.Business;
import com.x.processplatform.assemble.bam.ThisApplication;
import com.x.processplatform.assemble.bam.stub.PersonStub;
import com.x.processplatform.assemble.bam.stub.UnitStub;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.TaskCompleted_;
import com.x.processplatform.core.entity.content.Task_;

public class TimerOrganization extends ActionBase {

	public void execute(Business business) throws Exception {
		ActionOrganization.Wo wo = new ActionOrganization.Wo();
		Date start = this.getStart();
		Date current = new Date();
		wo.setUnit(this.unit(business, start, current));
		wo.setPerson(this.person(business, start, current));
		ThisApplication.state.setOrganization(wo);
	}

	private List<ActionOrganization.WoUnit> unit(Business business, Date start, Date current) throws Exception {
		List<ActionOrganization.WoUnit> list = new ArrayList<>();
		for (UnitStub stub : ThisApplication.state.getUnitStubs()) {
			List<String> us = new ArrayList<>();
			us.add(stub.getValue());
			us.addAll(business.organization().unit().listWithUnitSubNested(stub.getValue()));
			Long count = this.countWithUnit(business, start, us);
			Long expiredCount = this.countExpiredWithUnit(business, start, current, us);
			Long duration = this.durationWithUnit(business, start, current, us);
			Long completedCount = this.countCompletedWithUnit(business, start, us);
			Long completedExpiredCount = this.countExpiredCompletedWithUnit(business, start, us);
			ActionOrganization.WoUnit wo = new ActionOrganization.WoUnit();
			wo.setName(stub.getName());
			wo.setValue(stub.getValue());
			wo.setCount(count);
			wo.setExpiredCount(expiredCount);
			wo.setDuration(duration);
			wo.setCompletedCount(completedCount);
			wo.setCompletedExpiredCount(completedExpiredCount);
			list.add(wo);
		}
		return list;
	}

	private Long countWithUnit(Business business, Date start, List<String> units) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.greaterThan(root.get(Task_.startTime), start);
		p = cb.and(p, root.get(Task_.unit).in(units));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	private Long countExpiredWithUnit(Business business, Date start, Date current, List<String> units)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.greaterThan(root.get(Task_.startTime), start);
		p = cb.and(p, cb.lessThan(root.get(Task_.expireTime), current));
		p = cb.and(p, root.get(Task_.unit).in(units));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	private Long durationWithUnit(Business business, Date start, Date current, List<String> units) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Date> cq = cb.createQuery(Date.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.greaterThan(root.get(Task_.startTime), start);
		p = cb.and(p, root.get(Task_.unit).in(units));
		cq.select(root.get(Task_.startTime)).where(p);
		List<Date> os = em.createQuery(cq).getResultList();
		long duration = 0;
		for (Date o : os) {
			duration += current.getTime() - o.getTime();
		}
		/** 转化为分钟 */
		duration = duration / (1000L * 60L);
		return duration;
	}

	private Long countCompletedWithUnit(Business business, Date start, List<String> units) throws Exception {
		EntityManager em = business.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.greaterThan(root.get(TaskCompleted_.completedTime), start);
		p = cb.and(p, root.get(TaskCompleted_.unit).in(units));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	private Long countExpiredCompletedWithUnit(Business business, Date start, List<String> units) throws Exception {
		EntityManager em = business.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.greaterThan(root.get(TaskCompleted_.completedTime), start);
		p = cb.and(p, root.get(TaskCompleted_.unit).in(units));
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.expired), true));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	private List<ActionOrganization.WoPerson> person(Business business, Date start, Date current) throws Exception {
		List<ActionOrganization.WoPerson> list = new ArrayList<>();
		for (PersonStub stub : ThisApplication.state.getPersonStubs()) {
			Long count = this.countWithPerson(business, start, stub.getValue());
			Long expiredCount = this.countExpiredWithPerson(business, start, current, stub.getValue());
			Long duration = this.durationWithPerson(business, start, current, stub.getValue());
			Long completedCount = this.countCompletedWithPerson(business, start, stub.getValue());
			Long completedExpiredCount = this.countExpiredCompletedWithPerson(business, start, stub.getValue());
			ActionOrganization.WoPerson wo = new ActionOrganization.WoPerson();
			wo.setName(stub.getName());
			wo.setValue(stub.getValue());
			wo.setCount(count);
			wo.setExpiredCount(expiredCount);
			wo.setDuration(duration);
			wo.setCompletedCount(completedCount);
			wo.setCompletedExpiredCount(completedExpiredCount);
			list.add(wo);
		}
		list = list.stream().sorted(
				Comparator.comparing(ActionOrganization.WoPerson::getCount, Comparator.nullsLast(Long::compareTo)))
				.collect(Collectors.toList());
		return list;
	}

	private Long countWithPerson(Business business, Date start, String person) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.greaterThan(root.get(Task_.startTime), start);
		p = cb.and(p, cb.equal(root.get(Task_.person), person));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	private Long countExpiredWithPerson(Business business, Date start, Date current, String person) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.greaterThan(root.get(Task_.startTime), start);
		p = cb.and(p, cb.equal(root.get(Task_.person), person));
		p = cb.and(p, cb.lessThan(root.get(Task_.expireTime), current));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	private Long durationWithPerson(Business business, Date start, Date current, String person) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Date> cq = cb.createQuery(Date.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.greaterThan(root.get(Task_.startTime), start);
		p = cb.and(p, cb.equal(root.get(Task_.person), person));
		cq.select(root.get(Task_.startTime)).where(p);
		List<Date> os = em.createQuery(cq).getResultList();
		long duration = 0;
		for (Date o : os) {
			duration += current.getTime() - o.getTime();
		}
		duration = duration / (1000L * 60L);
		return duration;
	}

	private Long countCompletedWithPerson(Business business, Date start, String person) throws Exception {
		EntityManager em = business.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.greaterThan(root.get(TaskCompleted_.completedTime), start);
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.person), person));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	private Long countExpiredCompletedWithPerson(Business business, Date start, String person) throws Exception {
		EntityManager em = business.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.greaterThan(root.get(TaskCompleted_.completedTime), start);
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.person), person));
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.expired), true));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

}