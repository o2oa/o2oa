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
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.organization.core.entity.OauthCode;
import com.x.organization.core.entity.OauthCode_;

public class CleanupOauthCode extends AbstractJob {

	private static Logger logger = LoggerFactory.getLogger(CleanupOauthCode.class);

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			this.removeExpired(emc);
		} catch (Exception e) {
			logger.error(e);
			throw new JobExecutionException(e);
		}
	}

	private void removeExpired(EntityManagerContainer emc) throws Exception {
		EntityManager em = emc.get(OauthCode.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OauthCode> root = cq.from(OauthCode.class);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, -15);
		Predicate p = cb.not(cb.greaterThan(root.get(OauthCode_.createTime), cal.getTime()));
		cq.select(root.get(OauthCode_.id)).where(p);
		List<String> list = em.createQuery(cq).getResultList();
		for (int i = 0; i < list.size(); i++) {
			if (i % 100 == 0) {
				emc.beginTransaction(OauthCode.class);
			}
			OauthCode o = emc.find(list.get(i), OauthCode.class);
			emc.remove(o);
			if ((i % 100 == 99) || (i == (list.size() - 1))) {
				emc.commit();
			}
		}
	}

}
