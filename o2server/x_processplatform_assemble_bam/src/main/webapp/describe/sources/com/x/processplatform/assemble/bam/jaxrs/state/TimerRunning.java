package com.x.processplatform.assemble.bam.jaxrs.state;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.processplatform.assemble.bam.Business;
import com.x.processplatform.assemble.bam.ThisApplication;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.Task_;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.Work_;

public class TimerRunning extends ActionBase {

	public void execute(Business business) throws Exception {

		ActionRunning.Wo wo = new ActionRunning.Wo();
		Date start = this.getStart();
		Date current = new Date();
		wo.setTask(this.task(business, start, current));
		wo.setWork(this.work(business, start, current));
		ThisApplication.state.setRunning(wo);
	}

	private ActionRunning.WoTask task(Business business, Date start, Date current) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Task.class);
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
		ActionRunning.WoTask wo = new ActionRunning.WoTask();
		wo.setDuration(duration);
		wo.setHalfDay(halfDay);
		wo.setOneDay(oneDay);
		wo.setTwoDay(twoDay);
		wo.setThreeDay(threeDay);
		wo.setMoreDay(moreDay);
		return wo;
	}

	private ActionRunning.WoWork work(Business business, Date start, Date current) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Date> cq = cb.createQuery(Date.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.greaterThan(root.get(Work_.startTime), start);
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
		ActionRunning.WoWork o = new ActionRunning.WoWork();
		o.setDuration(duration);
		o.setThreeDay(threeDay);
		o.setOneWeek(oneWeek);
		o.setTwoWeek(twoWeek);
		o.setOneMonth(oneMonth);
		o.setMoreMonth(moreMonth);
		return o;
	}
}