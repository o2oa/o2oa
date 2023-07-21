package com.x.organization.assemble.authentication.schedule;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject_;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.organization.core.entity.log.TokenThreshold;

public class UpdateTokenThresholds extends AbstractJob {

	private static Logger logger = LoggerFactory.getLogger(UpdateTokenThresholds.class);

	private static Date lastUpdateDate;

	private static final int TOKENTHRESHOLDSMAXSIZE = 2000;

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		try {
			update();
		} catch (Exception e) {
			logger.error(e);
			throw new JobExecutionException(e);
		}
	}

	private void update() throws Exception {
		Date now = new Date();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EntityManager em = emc.get(TokenThreshold.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<TokenThreshold> cq = cb.createQuery(TokenThreshold.class);
			Root<TokenThreshold> root = cq.from(TokenThreshold.class);
			Predicate p = cb.conjunction();
			if (null != lastUpdateDate) {
				p = cb.and(p, cb.greaterThanOrEqualTo(root.get(JpaObject_.updateTime), lastUpdateDate));
			}
			List<TokenThreshold> list = em.createQuery(cq.select(root).where(p)).setMaxResults(TOKENTHRESHOLDSMAXSIZE)
					.getResultList();
			Map<String, Date> map = Config.resource_node_tokenThresholds();
			list.stream().forEach(o -> map.put(o.getPerson(), o.getThreshold()));
			stamp(now);
		}
	}

	private static void stamp(Date date) {
		lastUpdateDate = date;
	}
}