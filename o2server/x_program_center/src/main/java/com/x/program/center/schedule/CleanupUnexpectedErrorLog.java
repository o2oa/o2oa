package com.x.program.center.schedule;

import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.center.core.entity.UnexpectedErrorLog;
import com.x.program.center.core.entity.UnexpectedErrorLog_;

public class CleanupUnexpectedErrorLog extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(CleanupUnexpectedErrorLog.class);

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			if (pirmaryCenter()) {
				cleanupUnexpectedErrorLog();
			}
		} catch (Exception e) {
			logger.error(e);
			throw new JobExecutionException(e);
		}
	}

	private void cleanupUnexpectedErrorLog() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EntityManager em = emc.get(UnexpectedErrorLog.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<String> cq = cb.createQuery(String.class);
			Root<UnexpectedErrorLog> root = cq.from(UnexpectedErrorLog.class);
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, -7);
			Predicate p = cb.not(cb.greaterThan(root.get(UnexpectedErrorLog_.createTime), cal.getTime()));
			cq.select(root.get(UnexpectedErrorLog_.id)).where(p);
			List<String> list = em.createQuery(cq).getResultList();
			for (int i = 0; i < list.size(); i++) {
				if (i % 100 == 0) {
					emc.beginTransaction(UnexpectedErrorLog.class);
				}
				UnexpectedErrorLog o = emc.find(list.get(i), UnexpectedErrorLog.class);
				emc.remove(o);
				if ((i % 100 == 99) || (i == (list.size() - 1))) {
					emc.commit();
				}
			}
		}
	}
}
