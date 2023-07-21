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
import com.x.base.core.entity.JpaObject_;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_assemble_surface;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.base.core.project.utils.time.TimeStamp;
import com.x.processplatform.core.entity.content.Draft;
import com.x.processplatform.core.entity.content.Draft_;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.Work_;
import com.x.processplatform.service.processing.ThisApplication;

import fr.opensagres.poi.xwpf.converter.core.utils.StringUtils;

public class DeleteDraft extends AbstractJob {

	private static Logger logger = LoggerFactory.getLogger(DeleteDraft.class);

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		try {
			TimeStamp stamp = new TimeStamp();
			String draftSequence = null;
			String workSequence = null;
			AtomicInteger draftCount = new AtomicInteger();
			AtomicInteger workCount = new AtomicInteger();
			List<Draft> drafts = new ArrayList<>();
			List<Work> works = new ArrayList<>();
			do {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					drafts = this.listDraft(emc, draftSequence);
				}
				if (!drafts.isEmpty()) {
					draftSequence = drafts.get(drafts.size() - 1).getSequence();
					for (Draft draft : drafts) {
						try {
							try {
								ThisApplication.context().applications()
										.deleteQuery(x_processplatform_assemble_surface.class,
												Applications.joinQueryUri("draft", draft.getId()), draft.getId())
										.getData(WoId.class);
								draftCount.incrementAndGet();
							} catch (Exception e) {
								throw new ExceptionDeleteDraft(e, draft.getId(), draft.getTitle(), draft.getSequence());
							}
						} catch (Exception e) {
							logger.error(e);
						}
					}
				}
			} while (!drafts.isEmpty());
			do {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					works = this.listWork(emc, workSequence);
				}
				if (!works.isEmpty()) {
					workSequence = works.get(works.size() - 1).getSequence();
					for (Work work : works) {
						try {
							try {
								ThisApplication.context().applications()
										.deleteQuery(x_processplatform_service_processing.class,
												Applications.joinQueryUri("work", work.getId(), "draft"), work.getJob())
										.getData(WoId.class);
								workCount.incrementAndGet();
							} catch (Exception e) {
								throw new ExceptionDeleteDraft(e, work.getId(), work.getTitle(), work.getSequence());
							}
						} catch (Exception e) {
							logger.error(e);
						}
					}
				}
			} while (!works.isEmpty());
			logger.print("删除{}个处于拟稿环节的停滞工作, 删除{}个草稿. 耗时:{}.", workCount.intValue(), draftCount.intValue(),
					stamp.consumingMilliseconds());
		} catch (Exception e) {
			throw new JobExecutionException(e);
		}
	}

	private List<Work> listWork(EntityManagerContainer emc, String sequence) throws Exception {
		Date date = new Date();
		date = DateUtils.addMinutes(date, 0 - Config.processPlatform().getDeleteDraft().getThresholdMinutes());
		EntityManager em = emc.get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<Work> root = cq.from(Work.class);
		Path<String> idPath = root.get(Work_.id);
		Path<String> jobPath = root.get(Work_.job);
		Path<String> sequencePath = root.get(JpaObject_.sequence);
		Path<Date> sequenceActivityArrivedTime = root.get(Work_.activityArrivedTime);
		Predicate p = cb.lessThan(sequenceActivityArrivedTime, date);
		p = cb.and(p, cb.equal(root.get(Work_.workThroughManual), false));
		p = cb.and(p, cb.equal(root.get(Work_.workCreateType), Work.WORKCREATETYPE_SURFACE));
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

	private List<Draft> listDraft(EntityManagerContainer emc, String sequence) throws Exception {
		Date date = new Date();
		date = DateUtils.addMinutes(date, 0 - Config.processPlatform().getDeleteDraft().getThresholdMinutes());
		EntityManager em = emc.get(Draft.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<Draft> root = cq.from(Draft.class);
		Path<String> idPath = root.get(Draft_.id);
		Path<String> sequencePath = root.get(JpaObject_.sequence);
		Predicate p = cb.lessThan(root.get(JpaObject_.createTime), date);
		if (StringUtils.isNotEmpty(sequence)) {
			p = cb.and(p, cb.greaterThan(sequencePath, sequence));
		}
		cq.multiselect(idPath, sequencePath).where(p).orderBy(cb.asc(sequencePath));
		List<Tuple> os = em.createQuery(cq).setMaxResults(200).getResultList();
		List<Draft> list = new ArrayList<>();
		for (Tuple o : os) {
			Draft draft = new Draft();
			draft.setId(o.get(idPath));
			draft.setSequence(o.get(sequencePath));
			list.add(draft);
		}
		return list;
	}

}