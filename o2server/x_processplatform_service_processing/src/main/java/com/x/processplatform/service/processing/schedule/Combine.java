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

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject_;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.base.core.project.utils.time.TimeStamp;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkCompleted_;
import com.x.processplatform.service.processing.ThisApplication;

import org.apache.commons.lang3.time.DateUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import fr.opensagres.poi.xwpf.converter.core.utils.StringUtils;

public class Combine extends AbstractJob {

	private static Logger logger = LoggerFactory.getLogger(Combine.class);

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		try {
			TimeStamp stamp = new TimeStamp();
			List<WorkCompleted> targets = new ArrayList<>();
			String sequence = null;
			AtomicInteger count = new AtomicInteger();
			do {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					targets = this.list(emc, sequence);
				}
				if (!targets.isEmpty()) {
					sequence = targets.get(targets.size() - 1).getSequence();
					for (WorkCompleted workCompleted : targets) {
						try {
							try {
								ThisApplication.context().applications()
										.getQuery(x_processplatform_service_processing.class, Applications
												.joinQueryUri("workcompleted", workCompleted.getId(), "combine"),
												workCompleted.getJob())
										.getData(WoId.class);
								count.incrementAndGet();
							} catch (Exception e) {
								throw new ExceptionCombine(e, workCompleted.getId(), workCompleted.getTitle(),
										workCompleted.getSequence());
							}
						} catch (Exception e) {
							logger.error(e);
						}
					}
				}
			} while (!targets.isEmpty());
			logger.print("完成{}个已完成工作合并, 耗时:{}.", count.intValue(), stamp.consumingMilliseconds());
		} catch (Exception e) {
			throw new JobExecutionException(e);
		}
	}

	private List<WorkCompleted> list(EntityManagerContainer emc, String sequence) throws Exception {
		Date date = new Date();
		date = DateUtils.addDays(date, 0 - Config.processPlatform().getCombine().getThresholdDays());
		EntityManager em = emc.get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		Path<String> idPath = root.get(WorkCompleted_.id);
		Path<String> jobPath = root.get(WorkCompleted_.job);
		Path<String> sequencePath = root.get(JpaObject_.sequence);
		Predicate p = cb.or(cb.isNull(root.get(WorkCompleted_.merged)),
				cb.equal(root.get(WorkCompleted_.merged), false));
		p = cb.and(p, cb.lessThan(root.get(WorkCompleted_.completedTime), date));
		if (StringUtils.isNotEmpty(sequence)) {
			p = cb.and(p, cb.greaterThan(sequencePath, sequence));
		}
		cq.multiselect(idPath, jobPath, sequencePath).where(p).orderBy(cb.asc(sequencePath));
		List<Tuple> os = em.createQuery(cq).setMaxResults(100).getResultList();
		List<WorkCompleted> list = new ArrayList<>();
		for (Tuple o : os) {
			WorkCompleted workCompleted = new WorkCompleted();
			workCompleted.setId(o.get(idPath));
			workCompleted.setJob(o.get(jobPath));
			workCompleted.setSequence(o.get(sequencePath));
			list.add(workCompleted);
		}
		return list;
	}
}