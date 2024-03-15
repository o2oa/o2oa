package com.x.processplatform.assemble.surface.schedule;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject_;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.base.core.project.utils.time.TimeStamp;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.Task_;
import com.x.processplatform.core.express.service.processing.jaxrs.task.ActionExpireWo;

public class Expire extends AbstractJob {

	private static final Logger LOGGER = LoggerFactory.getLogger(Expire.class);

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		try {
			TimeStamp stamp = new TimeStamp();
			String sequence = null;
			List<Task> targets = new ArrayList<>();
			AtomicInteger count = new AtomicInteger();
			AtomicInteger pause = new AtomicInteger();
			do {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					targets = this.list(emc, sequence);
				}
				if (!targets.isEmpty()) {
					sequence = targets.get(targets.size() - 1).getSequence();
					for (Task task : targets) {
						// 如果是挂起状态那么就不再进行标志过期
						if (BooleanUtils.isNotTrue(task.getPause())) {
							expire(task);
							count.incrementAndGet();
						} else {
							pause.incrementAndGet();
						}
					}
				}
			} while (!targets.isEmpty());
			LOGGER.info("标识{}个过期待办, {}个待办处于挂起状态, 耗时:{}.", count.intValue(), pause.intValue(),
					stamp.consumingMilliseconds());
		} catch (Exception e) {
			throw new JobExecutionException(e);
		}
	}

	private void expire(Task task) {
		try {
			ThisApplication.context().applications()
					.getQuery(x_processplatform_service_processing.class,
							Applications.joinQueryUri("task", task.getId(), "expire"), task.getJob())
					.getData(ActionExpireWo.class);
		} catch (Exception e) {
			ExceptionExpire exceptionExpire = new ExceptionExpire(e, task.getId(), task.getTitle(), task.getSequence());
			LOGGER.error(exceptionExpire);
		}
	}

	private List<Task> list(EntityManagerContainer emc, String sequence) throws Exception {
		EntityManager em = emc.get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<Task> root = cq.from(Task.class);
		Path<String> idPath = root.get(Task_.id);
		Path<String> jobPath = root.get(Task_.job);
		Path<String> sequencePath = root.get(JpaObject_.sequence);
		Path<Boolean> pausePath = root.get(Task_.pause);
		Predicate p = cb.or(cb.isNull(root.get(Task_.expired)), cb.equal(root.get(Task_.expired), false));
		p = cb.and(p, cb.isNotNull(root.get(Task_.expireTime)));
		p = cb.and(p, cb.lessThanOrEqualTo(root.get(Task_.expireTime), new Date()));
		if (StringUtils.isNotEmpty(sequence)) {
			p = cb.and(p, cb.greaterThan(sequencePath, sequence));
		}
		cq.multiselect(idPath, jobPath, sequencePath, pausePath).where(p).orderBy(cb.asc(sequencePath));
		List<Tuple> os = em.createQuery(cq).setMaxResults(200).getResultList();
		List<Task> list = new ArrayList<>();
		for (Tuple o : os) {
			Task task = new Task();
			task.setId(o.get(idPath));
			task.setJob(o.get(jobPath));
			task.setSequence(o.get(sequencePath));
			task.setPause(o.get(pausePath));
			list.add(task);
		}
		return list;
	}

}