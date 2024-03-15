package com.x.processplatform.service.processing.schedule;

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

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject_;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.base.core.project.utils.time.TimeStamp;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.Task_;
import com.x.processplatform.service.processing.ThisApplication;

import fr.opensagres.poi.xwpf.converter.core.utils.StringUtils;

public class Urge extends AbstractJob {

	private static final Logger LOGGER = LoggerFactory.getLogger(Urge.class);

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		try {
			TimeStamp stamp = new TimeStamp();
			List<Task> targets = new ArrayList<>();
			String sequence = null;
			AtomicInteger count = new AtomicInteger();
			do {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					targets = this.list(emc, sequence);
				}
				if (!targets.isEmpty()) {
					sequence = targets.get(targets.size() - 1).getSequence();
					for (Task task : targets) {
						urge(task, count);
					}
				}
			} while (!targets.isEmpty());
			LOGGER.info("完成{}个待办的催办, 耗时:{}.", count.intValue(), stamp.consumingMilliseconds());
		} catch (Exception e) {
			throw new JobExecutionException(e);
		}
	}

	private void urge(Task task, AtomicInteger count) {
		try {
			ThisApplication.context().applications().getQuery(x_processplatform_service_processing.class,
					Applications.joinQueryUri("task", task.getId(), "urge"), task.getJob()).getData(WoId.class);
			count.incrementAndGet();
		} catch (Exception e) {
			LOGGER.error(new ExceptionUrge(e, task.getId(), task.getTitle(), task.getSequence()));
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
		Path<Date> urgeTimePath = root.get(Task_.urgeTime);
		Path<Boolean> urgedPath = root.get(Task_.urged);
		Predicate p = cb.or(cb.equal(urgedPath, false), cb.isNull(urgedPath));
		p = cb.and(p, cb.lessThan(urgeTimePath, new Date()));
		if (StringUtils.isNotEmpty(sequence)) {
			p = cb.and(p, cb.greaterThan(sequencePath, sequence));
		}
		cq.multiselect(idPath, jobPath, sequencePath).where(p).orderBy(cb.asc(sequencePath));
		List<Tuple> os = em.createQuery(cq).setMaxResults(200).getResultList();
		List<Task> list = new ArrayList<>();
		for (Tuple o : os) {
			Task task = new Task();
			task.setId(o.get(idPath));
			task.setJob(o.get(jobPath));
			task.setSequence(o.get(sequencePath));
			list.add(task);
		}
		return list;
	}
}