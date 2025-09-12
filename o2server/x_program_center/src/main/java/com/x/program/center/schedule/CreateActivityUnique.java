package com.x.program.center.schedule;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.Agent;
import com.x.processplatform.core.entity.element.Begin;
import com.x.processplatform.core.entity.element.Cancel;
import com.x.processplatform.core.entity.element.Choice;
import com.x.processplatform.core.entity.element.Delay;
import com.x.processplatform.core.entity.element.Embed;
import com.x.processplatform.core.entity.element.End;
import com.x.processplatform.core.entity.element.Invoke;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.Merge;
import com.x.processplatform.core.entity.element.Parallel;
import com.x.processplatform.core.entity.element.Publish;
import com.x.processplatform.core.entity.element.Service;
import com.x.processplatform.core.entity.element.Split;
import com.x.program.center.Business;

public class CreateActivityUnique extends AbstractJob {

	/**
	 * 升级更新,补充activity的unique值
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(CreateActivityUnique.class);

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			for (Class<? extends Activity> activity : List.of(Agent.class, Begin.class, Cancel.class, Choice.class,
					Delay.class, End.class, Embed.class, Invoke.class, Manual.class, Merge.class, Parallel.class,
					Publish.class, Service.class, Split.class)) {
				createUnique(business, activity);
			}
		} catch (Exception e) {
			throw new JobExecutionException(e);
		}
	}

	private <T extends Activity> void createUnique(Business business, Class<T> clazz) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(clazz);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(clazz);
		Root<T> root = cq.from(clazz);
		cq.where(cb.or(cb.equal(root.get(Activity.unique_FIELDNAME), ""),
				cb.isNull(root.get(Activity.unique_FIELDNAME))));
		List<T> os = em.createQuery(cq).getResultList();
		if (!os.isEmpty()) {
			emc.beginTransaction(clazz);
			for (T t : os) {
				t.setUnique(JpaObject.createId());
			}
			emc.commit();
			LOGGER.info("更新活动 {} 的unique值, 数量:{}", clazz.getName(), os.size());
		}
	}
}