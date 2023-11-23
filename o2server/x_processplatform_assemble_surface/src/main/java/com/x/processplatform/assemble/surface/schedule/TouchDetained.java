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
import org.apache.commons.lang3.time.DateUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject_;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_assemble_surface;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.base.core.project.utils.time.TimeStamp;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.Work_;
import com.x.processplatform.core.express.assemble.surface.jaxrs.work.V2TriggerProcessingWo;

import fr.opensagres.poi.xwpf.converter.core.utils.StringUtils;

public class TouchDetained extends AbstractJob {

	private static final Logger LOGGER = LoggerFactory.getLogger(TouchDetained.class);

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		try {
			TimeStamp stamp = new TimeStamp();
			List<Work> targets = new ArrayList<>();
			String sequence = null;
			AtomicInteger total = new AtomicInteger();
			AtomicInteger triggerProcessingCount = new AtomicInteger();
			do {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					targets = this.list(emc, sequence);
				}
				if (!targets.isEmpty()) {
					sequence = targets.get(targets.size() - 1).getSequence();
					triggerProcessing(targets, total, triggerProcessingCount);
				}
			} while (!targets.isEmpty());
			LOGGER.print("完成{}个停滞工作触发,{}个流转到下一个环节, 耗时:{}.", total.intValue(), triggerProcessingCount.intValue(),
					stamp.consumingMilliseconds());
		} catch (Exception e) {
			throw new JobExecutionException(e);
		}
	}

	private void triggerProcessing(List<Work> targets, AtomicInteger total, AtomicInteger triggerProcessingCount) {
		for (Work work : targets) {
			try {
				V2TriggerProcessingWo resp = ThisApplication.context().applications()
						.getQuery(x_processplatform_assemble_surface.class,
								Applications.joinQueryUri("work", "v2", work.getId(), "trigger", "processing"),
								work.getJob())
						.getData(V2TriggerProcessingWo.class);
				if (BooleanUtils.isTrue(resp.getValue())) {
					triggerProcessingCount.incrementAndGet();
				}
				total.incrementAndGet();
			} catch (Exception e) {
				LOGGER.error(new ExceptionTouchDetained(e, work.getId(), work.getTitle(), work.getSequence()));
			}
		}
	}

	private List<Work> list(EntityManagerContainer emc, String sequence) throws Exception {
		Date date = new Date();
		date = DateUtils.addMinutes(date, 0 - Config.processPlatform().getTouchDetained().getThresholdMinutes());
		EntityManager em = emc.get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<Work> root = cq.from(Work.class);
		Path<String> idPath = root.get(Work_.id);
		Path<String> jobPath = root.get(Work_.job);
		Path<String> sequencePath = root.get(JpaObject_.sequence);
		Path<Date> sequenceActivityArrivedTime = root.get(Work_.activityArrivedTime);
		Predicate p = cb.lessThan(sequenceActivityArrivedTime, date);
		if (StringUtils.isNotEmpty(sequence)) {
			p = cb.and(p, cb.greaterThan(sequencePath, sequence));
		}
		cq.multiselect(idPath, jobPath, sequencePath).where(p).orderBy(cb.asc(sequencePath));
		List<Tuple> os = em.createQuery(cq).setMaxResults(200).getResultList();
		List<Work> list = new ArrayList<>();
		for (Tuple o : os) {
			Work work = new Work();
			work.setId(o.get(idPath));
			work.setJob(o.get(jobPath));
			work.setSequence(o.get(sequencePath));
			list.add(work);
		}
		return list;
	}

}
