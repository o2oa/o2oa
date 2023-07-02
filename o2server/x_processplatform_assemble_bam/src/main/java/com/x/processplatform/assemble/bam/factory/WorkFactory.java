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
import com.x.processplatform.assemble.bam.stub.ProcessStub;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.Work_;

public class WorkFactory extends AbstractFactory {

	public WorkFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}



	public WorkDurationWithPeriodCountObject durationWithPeriodCount(Date start, Date current,
			ApplicationStub applicationStub) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Date> cq = cb.createQuery(Date.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.greaterThan(root.get(Work_.startTime), start);
		p = cb.and(p, cb.equal(root.get(Work_.application), applicationStub.getValue()));
		cq.select(root.get(Work_.startTime)).where(p);
		List<Date> os = em.createQuery(cq).getResultList();
		int threeDay = 0;
		int oneWeek = 0;
		int twoWeek = 0;
		int oneMonth = 0;
		int moreMonth = 0;
		long duration = 0;
		for (Date o : os) {
			long d = current.getTime() - o.getTime();
			if (d > (1000L * 60L * 60L * 24L * 30L)) {
				moreMonth++;
			} else if (d > (1000L * 60L * 60L * 24L * 30L)) {
				oneMonth++;
			} else if (d > (1000L * 60L * 60L * 24L * 30L)) {
				twoWeek++;
			} else if (d > (1000L * 60L * 60L * 24L * 30L)) {
				oneWeek++;
			} else {
				threeDay++;
			}
			duration += d;
		}
		duration = duration / (1000L * 60L);
		WorkDurationWithPeriodCountObject o = new WorkDurationWithPeriodCountObject();
		o.setDuration(duration);
		o.setThreeDay(threeDay);
		o.setOneWeek(oneWeek);
		o.setTwoWeek(twoWeek);
		o.setOneMonth(oneMonth);
		o.setMoreMonth(moreMonth);
		return o;
	}

	public WorkDurationWithPeriodCountObject durationWithPeriodCount(Date start, Date current, ProcessStub processStub)
			throws Exception {
		EntityManager em = this.entityManagerContainer().get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Date> cq = cb.createQuery(Date.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.greaterThan(root.get(Work_.startTime), start);
		p = cb.and(p, cb.equal(root.get(Work_.process), processStub.getValue()));
		cq.select(root.get(Work_.startTime)).where(p);
		List<Date> os = em.createQuery(cq).getResultList();
		int threeDay = 0;
		int oneWeek = 0;
		int twoWeek = 0;
		int oneMonth = 0;
		int moreMonth = 0;
		long duration = 0;
		for (Date o : os) {
			long d = current.getTime() - o.getTime();
			if (d > (1000L * 60L * 60L * 24L * 30L)) {
				moreMonth++;
			} else if (d > (1000L * 60L * 60L * 24L * 30L)) {
				oneMonth++;
			} else if (d > (1000L * 60L * 60L * 24L * 30L)) {
				twoWeek++;
			} else if (d > (1000L * 60L * 60L * 24L * 30L)) {
				oneWeek++;
			} else {
				threeDay++;
			}
			duration += d;
		}
		duration = duration / (1000L * 60L);
		WorkDurationWithPeriodCountObject o = new WorkDurationWithPeriodCountObject();
		o.setDuration(duration);
		o.setThreeDay(threeDay);
		o.setOneWeek(oneWeek);
		o.setTwoWeek(twoWeek);
		o.setOneMonth(oneMonth);
		o.setMoreMonth(moreMonth);
		return o;
	}

	public WorkDurationWithPeriodCountObject durationWithPeriodCount(Date start, Date current,
			ActivityStub activityStub) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Date> cq = cb.createQuery(Date.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.greaterThan(root.get(Work_.startTime), start);
		p = cb.and(p, cb.equal(root.get(Work_.activity), activityStub.getValue()));
		cq.select(root.get(Work_.startTime)).where(p);
		List<Date> os = em.createQuery(cq).getResultList();
		int threeDay = 0;
		int oneWeek = 0;
		int twoWeek = 0;
		int oneMonth = 0;
		int moreMonth = 0;
		long duration = 0;
		for (Date o : os) {
			long d = current.getTime() - o.getTime();
			if (d > (1000L * 60L * 60L * 24L * 30L)) {
				moreMonth++;
			} else if (d > (1000L * 60L * 60L * 24L * 30L)) {
				oneMonth++;
			} else if (d > (1000L * 60L * 60L * 24L * 30L)) {
				twoWeek++;
			} else if (d > (1000L * 60L * 60L * 24L * 30L)) {
				oneWeek++;
			} else {
				threeDay++;
			}
			duration += d;
		}
		duration = duration / (1000L * 60L);
		WorkDurationWithPeriodCountObject o = new WorkDurationWithPeriodCountObject();
		o.setDuration(duration);
		o.setThreeDay(threeDay);
		o.setOneWeek(oneWeek);
		o.setTwoWeek(twoWeek);
		o.setOneMonth(oneMonth);
		o.setMoreMonth(moreMonth);
		return o;
	}
}