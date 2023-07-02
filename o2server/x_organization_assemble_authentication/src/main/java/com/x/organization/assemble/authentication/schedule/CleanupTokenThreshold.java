package com.x.organization.assemble.authentication.schedule;

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
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.core.entity.log.TokenThreshold;
import com.x.organization.core.entity.log.TokenThreshold_;

public class CleanupTokenThreshold extends AbstractJob {

	private static Logger logger = LoggerFactory.getLogger(CleanupTokenThreshold.class);

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		try {
			cleanup();
		} catch (Exception e) {
			logger.error(e);
			throw new JobExecutionException(e);
		}
	}

	private void cleanup() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EntityManager em = emc.get(TokenThreshold.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<String> cq = cb.createQuery(String.class);
			Root<TokenThreshold> root = cq.from(TokenThreshold.class);
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, -Config.person().getTokenExpiredMinutes());
			Predicate p = cb.not(cb.greaterThan(root.get(TokenThreshold_.threshold), cal.getTime()));
			cq.select(root.get(TokenThreshold_.id)).where(p);
			List<String> list = em.createQuery(cq).getResultList();
			for (List<String> ids : ListTools.batch(list, 100)) {
				emc.beginTransaction(TokenThreshold.class);
				for (TokenThreshold o : emc.list(TokenThreshold.class, ids)) {
					emc.remove(o);
				}
				emc.commit();
			}
		}
	}

}