package com.x.processplatform.service.processing.schedule;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.time.DateUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.utils.time.TimeStamp;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkCompleted_;
import com.x.processplatform.service.processing.Business;
import com.x.query.core.entity.Item;
import com.x.query.core.entity.Item_;

public class DataMerge extends AbstractJob {

	private static Logger logger = LoggerFactory.getLogger(DataMerge.class);

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		TimeStamp stamp = new TimeStamp();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			DataItemConverter<Item> converter = new DataItemConverter<Item>(Item.class);
			WorkCompleted workCompleted = null;
			List<String> ids = new ArrayList<>();
			int count = 0;
			do {
				ids = this.list(business);
				for (String id : ids) {
					workCompleted = emc.find(id, WorkCompleted.class);
					if (null != workCompleted) {
						logger.print("数据合并任务, 标题: {}, id: {}.", workCompleted.getTitle(), workCompleted.getId());
						List<Item> items = this.items(business, workCompleted);
						JsonElement jsonElement = converter.assemble(items);
						emc.beginTransaction(WorkCompleted.class);
						workCompleted.setData(XGsonBuilder.toJson(jsonElement));
						workCompleted.setDataMerged(true);
						emc.commit();
						emc.beginTransaction(Item.class);
						for (Item item : items) {
							emc.remove(item, CheckRemoveType.all);
						}
						emc.commit();
						count++;
					}
				}
			} while (ListTools.isNotEmpty(ids));
			logger.print("共催办的任务 {} 个, 耗时:{}.", count, stamp.consumingMilliseconds());
		} catch (Exception e) {
			logger.error(e);
			throw new JobExecutionException(e);
		}
	}

	private List<Item> items(Business business, WorkCompleted workCompleted) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Item.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Item> cq = cb.createQuery(Item.class);
		Root<Item> root = cq.from(Item.class);
		Path<String> path = root.get(Item.bundle_FIELDNAME);
		Predicate p = cb.equal(path, workCompleted.getJob());
		p = cb.and(p, cb.equal(root.get(Item_.itemCategory), ItemCategory.pp));
		List<Item> list = em.createQuery(cq.where(p)).getResultList();
		return list;
	}

	private List<String> list(Business business) throws Exception {
		Date date = new Date();
		date = DateUtils.addDays(date, 0 - Config.processPlatform().getDataMerge().getPeriod());
		EntityManager em = business.entityManagerContainer().get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		Predicate p = cb.or(cb.isNull(root.get(WorkCompleted_.dataMerged)),
				cb.equal(root.get(WorkCompleted_.dataMerged), false));
		p = cb.and(p, cb.lessThan(root.get(WorkCompleted_.completedTime), date));
		cq.select(root.get(WorkCompleted_.id)).where(p).distinct(true);
		List<String> os = em.createQuery(cq.distinct(true)).setMaxResults(100).getResultList();
		return os;
	}

}