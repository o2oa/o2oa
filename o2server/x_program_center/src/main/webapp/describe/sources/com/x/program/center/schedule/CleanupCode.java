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
import com.x.program.center.core.entity.Code;
import com.x.program.center.core.entity.Code_;

public class CleanupCode extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(CleanupCode.class);

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			if (pirmaryCenter()) {
				cleanupCode();
			}
		} catch (Exception e) {
			logger.error(e);
			throw new JobExecutionException(e);
		}
	}

	private void cleanupCode() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EntityManager em = emc.get(Code.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<String> cq = cb.createQuery(String.class);
			Root<Code> root = cq.from(Code.class);
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, -30);
			Predicate p = cb.not(cb.greaterThan(root.get(Code_.createTime), cal.getTime()));
			cq.select(root.get(Code_.id)).where(p);
			List<String> list = em.createQuery(cq).getResultList();
			for (String id : list) {
				Code o = emc.find(id, Code.class);
				if (null != o) {
					emc.beginTransaction(Code.class);
					emc.remove(o);
					emc.commit();
				}
			}
		}
	}
}
