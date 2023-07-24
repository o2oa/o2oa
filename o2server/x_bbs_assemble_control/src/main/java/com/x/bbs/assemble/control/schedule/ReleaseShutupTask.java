package com.x.bbs.assemble.control.schedule;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.bbs.entity.BBSShutup;
import com.x.bbs.entity.BBSShutup_;
import org.quartz.JobExecutionContext;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.List;


/**
 * 定时处理解除禁用任务
 * @author LJ
 */
public class ReleaseShutupTask extends AbstractJob {

	private Logger logger = LoggerFactory.getLogger(ReleaseShutupTask.class);

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) {
		try {
			logger.info("开始定时处理解除禁用任务=======");
			List<String> list = this.getReleaseShutupList();
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				emc.beginTransaction(BBSShutup.class);
				emc.delete(BBSShutup.class, list);
				emc.commit();
			}
			logger.info("完成定时处理解除禁用任务=======");
		} catch (Exception e) {
			logger.warn("定时处理解除禁用任务异常：{}.", e.getMessage());
			logger.error(e);
		}
	}

	private List<String> getReleaseShutupList() throws Exception{
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			EntityManager em = emc.get( BBSShutup.class );
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<String> cq = cb.createQuery( String.class);
			Root<BBSShutup> root = cq.from( BBSShutup.class );
			Predicate p = cb.lessThan(root.get(BBSShutup_.unmuteDateTime), new Date());
			cq.select(root.get(BBSShutup_.id));
			return em.createQuery( cq.where(p)).getResultList();
		}
	}

}
