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

import org.apache.commons.lang3.time.DateUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
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

import fr.opensagres.poi.xwpf.converter.core.utils.StringUtils;

public class DataMerge extends AbstractJob {

	private static Logger logger = LoggerFactory.getLogger(DataMerge.class);

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
												.joinQueryUri("workcompleted", workCompleted.getId(), "data", "merge"),
												workCompleted.getJob())
										.getData(WoId.class);
								count.incrementAndGet();
							} catch (Exception e) {
								throw new ExceptionDataMerge(e, workCompleted.getId(), workCompleted.getTitle(),
										workCompleted.getSequence());
							}
						} catch (Exception e) {
							logger.error(e);
						}
					}
				}
			} while (!targets.isEmpty());
			logger.print("完成{}个已完成工作数据合并, 耗时:{}.", count.intValue(), stamp.consumingMilliseconds());
		} catch (Exception e) {
			throw new JobExecutionException(e);
		}
	}

	private List<WorkCompleted> list(EntityManagerContainer emc, String sequence) throws Exception {
		Date date = new Date();
		date = DateUtils.addDays(date, 0 - Config.processPlatform().getDataMerge().getThresholdDays());
		EntityManager em = emc.get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		Path<String> id_path = root.get(WorkCompleted_.id);
		Path<String> job_path = root.get(WorkCompleted_.job);
		Path<String> sequence_path = root.get(WorkCompleted_.sequence);
		Predicate p = cb.or(cb.isNull(root.get(WorkCompleted_.dataMerged)),
				cb.equal(root.get(WorkCompleted_.dataMerged), false));
		p = cb.and(p, cb.lessThan(root.get(WorkCompleted_.completedTime), date));
		if (StringUtils.isNotEmpty(sequence)) {
			p = cb.and(p, cb.greaterThan(sequence_path, sequence));
		}
		cq.multiselect(id_path, job_path, sequence_path).where(p).orderBy(cb.asc(sequence_path));
		List<Tuple> os = em.createQuery(cq).setMaxResults(200).getResultList();
		List<WorkCompleted> list = new ArrayList<>();
		for (Tuple o : os) {
			WorkCompleted workCompleted = new WorkCompleted();
			workCompleted.setId(o.get(id_path));
			workCompleted.setJob(o.get(job_path));
			workCompleted.setSequence(o.get(sequence_path));
			list.add(workCompleted);
		}
		return list;
	}
}