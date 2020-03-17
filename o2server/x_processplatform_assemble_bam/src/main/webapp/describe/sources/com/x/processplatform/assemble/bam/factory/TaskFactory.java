package com.x.processplatform.assemble.bam.factory;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.processplatform.assemble.bam.AbstractFactory;
import com.x.processplatform.assemble.bam.Business;
import com.x.processplatform.assemble.bam.stub.ActivityStub;
import com.x.processplatform.assemble.bam.stub.ApplicationStub;
import com.x.processplatform.assemble.bam.stub.PersonStub;
import com.x.processplatform.assemble.bam.stub.ProcessStub;
import com.x.processplatform.assemble.bam.stub.UnitStub;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.Task_;

public class TaskFactory extends AbstractFactory {

	public TaskFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	public Long count(Date start, UnitStub unitStub) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.greaterThan(root.get(Task_.startTime), start);
		p = cb.and(p, cb.equal(root.get(Task_.unit), unitStub.getValue()));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public Long count(Date start, PersonStub personStub) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.greaterThan(root.get(Task_.startTime), start);
		p = cb.and(p, cb.equal(root.get(Task_.person), personStub.getValue()));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public Long expiredCount(Date start, Date current, UnitStub unitStub) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.greaterThan(root.get(Task_.startTime), start);
		p = cb.and(p, cb.equal(root.get(Task_.unit), unitStub.getValue()));
		p = cb.and(p, cb.lessThan(root.get(Task_.expireTime), current));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}


	public Long duration(Date start, Date current, UnitStub unitStub) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Date> cq = cb.createQuery(Date.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.greaterThan(root.get(Task_.startTime), start);
		p = cb.and(p, cb.equal(root.get(Task_.unit), unitStub.getValue()));
		cq.select(root.get(Task_.startTime)).where(p);
		List<Date> os = em.createQuery(cq).getResultList();
		long duration = 0;
		for (Date o : os) {
			duration += current.getTime() - o.getTime();
		}
		duration = duration / (1000L * 60L);
		return duration;
	}

	public TaskDurationWithPeriodCountObject durationWithPeriodCount(Date start, Date current) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Date> cq = cb.createQuery(Date.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.greaterThan(root.get(Task_.startTime), start);
		cq.select(root.get(Task_.startTime)).where(p);
		List<Date> os = em.createQuery(cq).getResultList();
		int halfDay = 0;
		int oneDay = 0;
		int twoDay = 0;
		int threeDay = 0;
		int moreDay = 0;
		long duration = 0;
		for (Date o : os) {
			long d = current.getTime() - o.getTime();
			if (d > (1000L * 60L * 60L * 24L * 3L)) {
				moreDay++;
			} else if (d > (1000L * 60L * 60L * 24L * 2L)) {
				threeDay++;
			} else if (d > (1000L * 60L * 60L * 24L)) {
				twoDay++;
			} else if (d > (1000L * 60L * 60L * 12L)) {
				oneDay++;
			} else {
				halfDay++;
			}
			duration += d;
		}
		duration = duration / (1000L * 60L);
		TaskDurationWithPeriodCountObject o = new TaskDurationWithPeriodCountObject();
		o.setDuration(duration);
		o.setHalfDay(halfDay);
		o.setOneDay(oneDay);
		o.setTwoDay(twoDay);
		o.setThreeDay(threeDay);
		o.setMoreDay(moreDay);
		return o;
	}

	public TaskDurationWithPeriodCountObject durationWithPeriodCount(Date start, Date current,
			ApplicationStub applicationStub) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Date> cq = cb.createQuery(Date.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.greaterThan(root.get(Task_.startTime), start);
		p = cb.and(p, cb.equal(root.get(Task_.application), applicationStub.getValue()));
		cq.select(root.get(Task_.startTime)).where(p);
		List<Date> os = em.createQuery(cq).getResultList();
		int halfDay = 0;
		int oneDay = 0;
		int twoDay = 0;
		int threeDay = 0;
		int moreDay = 0;
		long duration = 0;
		for (Date o : os) {
			long d = current.getTime() - o.getTime();
			if (d > (1000L * 60L * 60L * 24L * 3L)) {
				moreDay++;
			} else if (d > (1000L * 60L * 60L * 24L * 2L)) {
				threeDay++;
			} else if (d > (1000L * 60L * 60L * 24L)) {
				twoDay++;
			} else if (d > (1000L * 60L * 60L * 12L)) {
				oneDay++;
			} else {
				halfDay++;
			}
			duration += d;
		}
		duration = duration / (1000L * 60L);
		TaskDurationWithPeriodCountObject o = new TaskDurationWithPeriodCountObject();
		o.setDuration(duration);
		o.setHalfDay(halfDay);
		o.setOneDay(oneDay);
		o.setTwoDay(twoDay);
		o.setThreeDay(threeDay);
		o.setMoreDay(moreDay);
		return o;
	}

	public TaskDurationWithPeriodCountObject durationWithPeriodCount(Date start, Date current, ProcessStub processStub)
			throws Exception {
		EntityManager em = this.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Date> cq = cb.createQuery(Date.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.greaterThan(root.get(Task_.startTime), start);
		p = cb.and(p, cb.equal(root.get(Task_.process), processStub.getValue()));
		cq.select(root.get(Task_.startTime)).where(p);
		List<Date> os = em.createQuery(cq).getResultList();
		int halfDay = 0;
		int oneDay = 0;
		int twoDay = 0;
		int threeDay = 0;
		int moreDay = 0;
		long duration = 0;
		for (Date o : os) {
			long d = current.getTime() - o.getTime();
			if (d > (1000L * 60L * 60L * 24L * 3L)) {
				moreDay++;
			} else if (d > (1000L * 60L * 60L * 24L * 2L)) {
				threeDay++;
			} else if (d > (1000L * 60L * 60L * 24L)) {
				twoDay++;
			} else if (d > (1000L * 60L * 60L * 12L)) {
				oneDay++;
			} else {
				halfDay++;
			}
			duration += d;
		}
		duration = duration / (1000L * 60L);
		TaskDurationWithPeriodCountObject o = new TaskDurationWithPeriodCountObject();
		o.setDuration(duration);
		o.setHalfDay(halfDay);
		o.setOneDay(oneDay);
		o.setTwoDay(twoDay);
		o.setThreeDay(threeDay);
		o.setMoreDay(moreDay);
		return o;
	}

	public TaskDurationWithPeriodCountObject durationWithPeriodCount(Date start, Date current,
			ActivityStub activityStub) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Date> cq = cb.createQuery(Date.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.greaterThan(root.get(Task_.startTime), start);
		p = cb.and(p, cb.equal(root.get(Task_.activity), activityStub.getValue()));
		cq.select(root.get(Task_.startTime)).where(p);
		List<Date> os = em.createQuery(cq).getResultList();
		int halfDay = 0;
		int oneDay = 0;
		int twoDay = 0;
		int threeDay = 0;
		int moreDay = 0;
		long duration = 0;
		for (Date o : os) {
			long d = current.getTime() - o.getTime();
			if (d > (1000L * 60L * 60L * 24L * 3L)) {
				moreDay++;
			} else if (d > (1000L * 60L * 60L * 24L * 2L)) {
				threeDay++;
			} else if (d > (1000L * 60L * 60L * 24L)) {
				twoDay++;
			} else if (d > (1000L * 60L * 60L * 12L)) {
				oneDay++;
			} else {
				halfDay++;
			}
			duration += d;
		}
		duration = duration / (1000L * 60L);
		TaskDurationWithPeriodCountObject o = new TaskDurationWithPeriodCountObject();
		o.setDuration(duration);
		o.setHalfDay(halfDay);
		o.setOneDay(oneDay);
		o.setTwoDay(twoDay);
		o.setThreeDay(threeDay);
		o.setMoreDay(moreDay);
		return o;
	}

}