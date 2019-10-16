package com.x.organization.assemble.personal.schedule;

import java.util.Date;
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
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.organization.core.entity.accredit.Empower;
import com.x.organization.core.entity.accredit.Empower_;

public class DisableExpiredEmpower extends AbstractJob {

	private static Logger logger = LoggerFactory.getLogger(DisableExpiredEmpower.class);

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			this.disableExpired(emc);
		} catch (Exception e) {
			logger.error(e);
			throw new JobExecutionException(e);
		}
	}

	private void disableExpired(EntityManagerContainer emc) throws Exception {
		EntityManager em = emc.get(Empower.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Empower> cq = cb.createQuery(Empower.class);
		Root<Empower> root = cq.from(Empower.class);
		Predicate p = cb.equal(root.get(Empower_.enable), true);
		p = cb.and(p, cb.lessThan(root.get(Empower_.completedTime), new Date()));
		List<Empower> os = em.createQuery(cq).getResultList();
		if (!os.isEmpty()) {
			emc.beginTransaction(Empower.class);
			for (Empower o : os) {
				o.setEnable(false);
			}
			emc.commit();
			ApplicationCache.notify(Empower.class);
		}

	}
}
