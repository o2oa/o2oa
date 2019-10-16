package com.x.processplatform.service.processing.schedule;

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
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.base.core.project.utils.time.TimeStamp;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.Task_;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.MessageFactory;

public class Urge extends AbstractJob {

	private static Logger logger = LoggerFactory.getLogger(Urge.class);

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		TimeStamp stamp = new TimeStamp();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			List<String> ids = this.list_urge_task(business);
			for (String id : ids) {
				Task task = emc.find(id, Task.class);
				if (null != task) {
					try {
						logger.print("催办任务, 用户: {}, 标题: {}, id: {}.", task.getPerson(), task.getTitle(), task.getId());
						emc.beginTransaction(Task.class);
						task.setUrged(true);
						emc.commit();
						MessageFactory.task_urge(task);
					} catch (Exception e) {
						logger.error(e);
					}
				}
			}
			logger.print("共催办的任务 {} 个, 耗时:{}.", ids.size(), stamp.consumingMilliseconds());
		} catch (Exception e) {
			logger.error(e);
			throw new JobExecutionException(e);
		}
	}

	private List<String> list_urge_task(Business business) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Task> root = cq.from(Task.class);
		Predicate p = cb.or(cb.isNull(root.get(Task_.urged)), cb.equal(root.get(Task_.urged), false));
		p = cb.and(p, cb.lessThanOrEqualTo(root.get(Task_.urgeTime), new Date()));
		cq.select(root.get(Task_.id)).where(p).distinct(true);
		List<String> os = em.createQuery(cq).getResultList();
		return os;
	}

}