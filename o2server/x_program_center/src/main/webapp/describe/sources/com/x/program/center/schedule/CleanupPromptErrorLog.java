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
import com.x.program.center.core.entity.PromptErrorLog;
import com.x.program.center.core.entity.PromptErrorLog_;

public class CleanupPromptErrorLog extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(CleanupPromptErrorLog.class);

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			if (pirmaryCenter()) {
				cleanupPromptErrorLog();
			}
		} catch (Exception e) {
			logger.error(e);
			throw new JobExecutionException(e);
		}
	}

	private void cleanupPromptErrorLog() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EntityManager em = emc.get(PromptErrorLog.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<String> cq = cb.createQuery(String.class);
			Root<PromptErrorLog> root = cq.from(PromptErrorLog.class);
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, -7);
			Predicate p = cb.not(cb.greaterThan(root.get(PromptErrorLog_.createTime), cal.getTime()));
			cq.select(root.get(PromptErrorLog_.id)).where(p);
			List<String> list = em.createQuery(cq).getResultList();
			for (int i = 0; i < list.size(); i++) {
				if (i % 100 == 0) {
					emc.beginTransaction(PromptErrorLog.class);
				}
				PromptErrorLog o = emc.find(list.get(i), PromptErrorLog.class);
				emc.remove(o);
				if ((i % 100 == 99) || (i == (list.size() - 1))) {
					emc.commit();
				}
			}
		}
	}

}
