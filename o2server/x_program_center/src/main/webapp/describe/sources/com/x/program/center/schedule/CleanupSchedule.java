package com.x.program.center.schedule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.ReportToCenter;
import com.x.program.center.Business;
import com.x.program.center.core.entity.Schedule;
import com.x.program.center.core.entity.ScheduleLocal;
import com.x.program.center.core.entity.ScheduleLocal_;
import com.x.program.center.core.entity.ScheduleLog;
import com.x.program.center.core.entity.ScheduleLog_;
import com.x.program.center.core.entity.Schedule_;

public class CleanupSchedule implements Job {

	private static Logger logger = LoggerFactory.getLogger(CleanupSchedule.class);

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			this.clearScheduleLocal(business);
			this.clearSchedule(business);
			this.clearScheduleLog(business);
		} catch (Exception e) {
			e.printStackTrace();
			throw new JobExecutionException(e);
		}
	}

	private void clearScheduleLocal(Business business) throws Exception {
		List<ScheduleLocal> list = new ArrayList<>();
		do {
			EntityManagerContainer emc = business.entityManagerContainer();
			EntityManager em = emc.get(ScheduleLocal.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ScheduleLocal> cq = cb.createQuery(ScheduleLocal.class);
			Root<ScheduleLocal> root = cq.from(ScheduleLocal.class);
			Calendar threshold = Calendar.getInstance();
			threshold.add(Calendar.SECOND, -(ReportToCenter.INTERVAL * 2));
			Predicate p = cb.lessThan(root.get(ScheduleLocal_.reportTime), threshold.getTime());
			list = em.createQuery(cq.select(root).where(p)).setMaxResults(10000).getResultList();
			if (!list.isEmpty()) {
				emc.beginTransaction(ScheduleLocal.class);
				list.stream().forEach(o -> {
					try {
						emc.remove(o, CheckRemoveType.all);
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
				emc.commit();
			}
		} while (!list.isEmpty());
	}

	private void clearSchedule(Business business) throws Exception {
		List<Schedule> list = new ArrayList<>();
		do {
			EntityManagerContainer emc = business.entityManagerContainer();
			EntityManager em = emc.get(Schedule.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Schedule> cq = cb.createQuery(Schedule.class);
			Root<Schedule> root = cq.from(Schedule.class);
			Calendar threshold = Calendar.getInstance();
			threshold.add(Calendar.SECOND, -(ReportToCenter.INTERVAL * 2));
			Predicate p = cb.lessThan(root.get(Schedule_.reportTime), threshold.getTime());
			list = em.createQuery(cq.select(root).where(p)).setMaxResults(10000).getResultList();
			if (!list.isEmpty()) {
				emc.beginTransaction(Schedule.class);
				list.stream().forEach(o -> {
					try {
						emc.remove(o, CheckRemoveType.all);
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
				emc.commit();
			}
		} while (!list.isEmpty());
	}

	private void clearScheduleLog(Business business) throws Exception {
		List<ScheduleLog> list = new ArrayList<>();
		do {
			EntityManagerContainer emc = business.entityManagerContainer();
			EntityManager em = emc.get(ScheduleLog.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ScheduleLog> cq = cb.createQuery(ScheduleLog.class);
			Root<ScheduleLog> root = cq.from(ScheduleLog.class);
			Calendar threshold = Calendar.getInstance();
			threshold.add(Calendar.DATE, -2);
			Predicate p = cb.lessThan(root.get(ScheduleLog_.createTime), threshold.getTime());
			list = em.createQuery(cq.select(root).where(p)).setMaxResults(10000).getResultList();
			if (!list.isEmpty()) {
				emc.beginTransaction(ScheduleLog.class);
				list.stream().forEach(o -> {
					try {
						emc.remove(o, CheckRemoveType.all);
					} catch (Exception e) {
						logger.error(e);
					}
				});
				emc.commit();
			}
		} while (!list.isEmpty());
	}

}