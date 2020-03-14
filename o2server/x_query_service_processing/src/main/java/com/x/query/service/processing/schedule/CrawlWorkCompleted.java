package com.x.query.service.processing.schedule;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_query_service_processing;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.base.core.project.utils.time.TimeStamp;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkCompleted_;
import com.x.query.core.entity.segment.Entry;
import com.x.query.core.entity.segment.Entry_;
import com.x.query.core.express.program.Arguments;
import com.x.query.service.processing.Business;
import com.x.query.service.processing.ThisApplication;

public class CrawlWorkCompleted extends AbstractJob {

	private static Logger logger = LoggerFactory.getLogger(CrawlWorkCompleted.class);

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		TimeStamp stamp = new TimeStamp();
		List<String> add_workCompleteds = null;
		List<String> update_workCompleteds = null;
		List<String> update_references = null;
		List<String> updates = null;
		Long workCompletedCount = null;
		Long entryCount = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			workCompletedCount = emc.count(WorkCompleted.class);
			entryCount = emc.countEqual(Entry.class, Entry.type_FIELDNAME, Entry.TYPE_WORKCOMPLETED);
			add_workCompleteds = this.listAddWorkCompleted(business);
			update_workCompleteds = this.listUpdateWorkCompleted(business);
			update_references = this.listUpdateEntryReference(business);
			updates = ListUtils.sum(ListUtils.sum(add_workCompleteds, update_workCompleteds), update_references);
		} catch (Exception e) {
			logger.error(e);
			throw new JobExecutionException(e);
		}
		this.update(update_references);
		logger.print(
				"完成工作索引器运行完成, 已完成工作总数:{}, 新索引已完成工作数量:{}, 轮询更新已完成工作数量:{}, 已索引条目总数:{}, 已索引条目更新数量:{}, 合并更新数量:{}, 数量限制:{}, 耗时{}.",
				workCompletedCount, add_workCompleteds.size(), update_workCompleteds.size(), entryCount,
				update_references.size(), updates.size(), Config.query().getCrawlWorkCompleted().getCount(),
				stamp.consumingMilliseconds());
	}

	private void update(List<String> references) throws Exception {
		for (String reference : references) {
			try {
				ThisApplication.context().applications().getQuery(x_query_service_processing.class,
						Applications.joinQueryUri("segment", "crawl", "workcompleted", reference), reference);
			} catch (Exception e) {
				logger.error(e);
			}
		}
	}

	/**
	 * 新增数据
	 */
	private List<String> listAddWorkCompleted(Business business) throws Exception {
		EntityManager em = business.entityManagerContainer().get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		cq.select(root.get(WorkCompleted_.id)).orderBy(cb.desc(root.get(WorkCompleted_.sequence)));
		List<String> os = em.createQuery(cq).setMaxResults(Config.query().getCrawlWorkCompleted().getCount())
				.getResultList();
		return os;
	}

	/**
	 * 定时进行轮询保证旧数据能定期进行更新
	 */
	private List<String> listUpdateWorkCompleted(Business business) throws Exception {
		EntityManager em = business.entityManagerContainer().get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		Predicate p = cb.conjunction();
		String sequence = Arguments.getCrawlUpdateWorkCompleted();
		if (StringUtils.isNotEmpty(sequence)) {
			p = cb.and(cb.lessThan(root.get(WorkCompleted.sequence_FIELDNAME), sequence));
		}
		cq.select(root.get(WorkCompleted_.id)).where(p).orderBy(cb.desc(root.get(WorkCompleted_.sequence)));
		Integer count = Config.query().getCrawlWorkCompleted().getCount() /4;
		List<String> os = em.createQuery(cq).setMaxResults(count).getResultList();
		if (os.size() == count) {
			Arguments.setCrawlUpdateWorkCompleted(os.get(os.size() - 1));
		} else {
			Arguments.setCrawlUpdateWorkCompleted("");
		}
		return os;
	}

	/**
	 * 更新旧的已索引条目
	 */
	private List<String> listUpdateEntryReference(Business business) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Entry.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Entry> root = cq.from(Entry.class);
		Predicate p = cb.equal(root.get(Entry_.type), Entry.TYPE_WORKCOMPLETED);
		cq.select(root.get(Entry_.reference)).where(p).orderBy(cb.asc(root.get(Entry_.sequence)));
		Integer count = Config.query().getCrawlWorkCompleted().getCount() / 4;
		List<String> os = em.createQuery(cq).setMaxResults(count).getResultList();
		return os;
	}
}