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
import com.x.program.center.Business;
import com.x.program.center.core.entity.ScheduleLog;
import com.x.program.center.core.entity.ScheduleLog_;

public class CleanupScheduleLog implements Job {

	private static Logger logger = LoggerFactory.getLogger(CleanupScheduleLog.class);

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				this.clearScheduleLog(business);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new JobExecutionException(e);
		}

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
			threshold.add(Calendar.DATE, -7);
			Predicate p = cb.lessThan(root.get(ScheduleLog_.createTime), threshold.getTime());
			list = em.createQuery(cq.select(root).where(p)).setMaxResults(2000).getResultList();
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