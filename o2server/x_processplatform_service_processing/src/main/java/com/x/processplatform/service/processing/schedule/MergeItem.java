package com.x.processplatform.service.processing.schedule;

import java.util.List;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject_;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.dataitem.DataItem;
import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkCompleted_;
import com.x.processplatform.core.entity.log.MergeItemPlan;
import com.x.processplatform.core.entity.log.MergeItemPlan_;
import com.x.query.core.entity.Item;

public class MergeItem extends AbstractJob {

	private static Logger logger = LoggerFactory.getLogger(MergeItem.class);
	protected static Gson gson = XGsonBuilder.instance();

	private static final Integer BATCH_SIZE = 100;

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			MergeItemPlan mergeItemPlan = this.getMergeItemPlan(emc);
			if (Objects.nonNull(mergeItemPlan)) {
				if (Objects.isNull(mergeItemPlan.getEstimatedCount()) || mergeItemPlan.getEstimatedCount() < 0) {
					Long estimatedCount = this.estimatedCount(emc, mergeItemPlan);
					emc.beginTransaction(MergeItemPlan.class);
					mergeItemPlan.setEstimatedCount(estimatedCount);
					emc.check(mergeItemPlan, CheckPersistType.all);
					emc.commit();
				}
				boolean completed = this.merge(emc, mergeItemPlan);
				if (completed) {
					emc.beginTransaction(MergeItemPlan.class);
					mergeItemPlan.setStatus(MergeItemPlan.STATUS_COMPLETED);
					emc.check(mergeItemPlan, CheckPersistType.all);
					emc.commit();
				}
			}
		}
	}

	private boolean merge(EntityManagerContainer emc, MergeItemPlan mergeItemPlan) throws Exception {
		int loop = 0;
		WorkCompleted workCompleted;
		do {
			workCompleted = this.getWorkCompleted(emc, mergeItemPlan);
			if (Objects.nonNull(workCompleted)) {
				logger.print("开始合并已完成工作数据条目, 标识:{}, 标题:{}.", workCompleted.getId(), workCompleted.getTitle());
				this.mergeItem(emc, workCompleted);
				emc.beginTransaction(MergeItemPlan.class);
				Long count = mergeItemPlan.getCount();
				if (Objects.isNull(count)) {
					count = 0L;
				}
				mergeItemPlan.setCount(count + 1);
				emc.commit();
				loop++;
			}
		} while (Objects.nonNull(workCompleted) && loop < BATCH_SIZE);
		return (loop != BATCH_SIZE);
	}

	private void mergeItem(EntityManagerContainer emc, WorkCompleted workCompleted) throws Exception {
		String job = workCompleted.getJob();
		emc.beginTransaction(WorkCompleted.class);
		List<Item> os = emc.listEqualAndEqual(Item.class, DataItem.bundle_FIELDNAME, job,
				DataItem.itemCategory_FIELDNAME, ItemCategory.pp);
		DataItemConverter<Item> converter = new DataItemConverter<>(Item.class);
		JsonElement jsonElement = converter.assemble(os);
		workCompleted.setData(gson.fromJson(jsonElement, Data.class));
		emc.commit();
		emc.beginTransaction(Item.class);
		os = emc.listEqualAndEqual(Item.class, DataItem.bundle_FIELDNAME, job, DataItem.itemCategory_FIELDNAME,
				ItemCategory.pp);
		for (Item item : os) {
			emc.remove(item);
		}
		emc.commit();
		emc.beginTransaction(WorkCompleted.class);
		workCompleted.setMerged(true);
		emc.commit();
		logger.print("已完成工作数据条目合并, 标题:{}.", workCompleted.getTitle());
	}

	private MergeItemPlan getMergeItemPlan(EntityManagerContainer emc) throws Exception {
		EntityManager em = emc.get(MergeItemPlan.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<MergeItemPlan> cq = cb.createQuery(MergeItemPlan.class);
		Root<MergeItemPlan> root = cq.from(MergeItemPlan.class);
		Predicate p = cb.equal(root.get(MergeItemPlan_.status), MergeItemPlan.STATUS_MERGING);
		p = cb.and(p, cb.equal(root.get(MergeItemPlan_.enable), true));
		cq.select(root).where(p).orderBy(cb.asc(root.get(JpaObject_.createTime)));
		List<MergeItemPlan> os = em.createQuery(cq).setMaxResults(1).getResultList();
		if (os.isEmpty()) {
			return null;
		} else {
			return os.get(0);
		}
	}

	private WorkCompleted getWorkCompleted(EntityManagerContainer emc, MergeItemPlan mergeItemPlan) throws Exception {
		EntityManager em = emc.get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<WorkCompleted> cq = cb.createQuery(WorkCompleted.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		Predicate p = cb.or(cb.isNull(root.get(WorkCompleted.merged_FIELDNAME)),
				cb.equal(root.get(WorkCompleted.merged_FIELDNAME), false));
		if (StringUtils.isNotBlank(mergeItemPlan.getApplication())) {
			p = cb.and(p, cb.equal(root.get(WorkCompleted_.application), mergeItemPlan.getApplication()));
		}
		if (StringUtils.isNotBlank(mergeItemPlan.getProcess())) {
			p = cb.and(p, cb.equal(root.get(WorkCompleted_.process), mergeItemPlan.getProcess()));
		}
		if (Objects.nonNull(mergeItemPlan.getStartTime())) {
			p = cb.and(p,
					cb.greaterThanOrEqualTo(root.get(WorkCompleted_.completedTime), mergeItemPlan.getStartTime()));
		}
		if (Objects.nonNull(mergeItemPlan.getCompletedTime())) {
			p = cb.and(p, cb.lessThan(root.get(WorkCompleted_.completedTime), mergeItemPlan.getCompletedTime()));
		}
		cq.select(root).where(p).orderBy(cb.asc(root.get(JpaObject_.createTime)));
		List<WorkCompleted> os = em.createQuery(cq).setMaxResults(1).getResultList();
		if (os.isEmpty()) {
			return null;
		} else {
			return os.get(0);
		}
	}

	private Long estimatedCount(EntityManagerContainer emc, MergeItemPlan mergeItemPlan) throws Exception {
		EntityManager em = emc.get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		Predicate p = cb.or(cb.isNull(root.get(WorkCompleted.merged_FIELDNAME)),
				cb.equal(root.get(WorkCompleted.merged_FIELDNAME), false));
		if (StringUtils.isNotBlank(mergeItemPlan.getApplication())) {
			p = cb.and(p, cb.equal(root.get(WorkCompleted_.application), mergeItemPlan.getApplication()));
		}
		if (StringUtils.isNotBlank(mergeItemPlan.getProcess())) {
			p = cb.and(p, cb.equal(root.get(WorkCompleted_.process), mergeItemPlan.getProcess()));
		}
		if (Objects.nonNull(mergeItemPlan.getStartTime())) {
			p = cb.and(p,
					cb.greaterThanOrEqualTo(root.get(WorkCompleted_.completedTime), mergeItemPlan.getStartTime()));
		}
		if (Objects.nonNull(mergeItemPlan.getCompletedTime())) {
			p = cb.and(p, cb.lessThan(root.get(WorkCompleted_.completedTime), mergeItemPlan.getCompletedTime()));
		}
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}
}