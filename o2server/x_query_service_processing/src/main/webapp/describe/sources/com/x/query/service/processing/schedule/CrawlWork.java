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
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.Work_;
import com.x.query.core.entity.segment.Entry;
import com.x.query.core.entity.segment.Entry_;
import com.x.query.core.express.program.Arguments;
import com.x.query.service.processing.Business;
import com.x.query.service.processing.ThisApplication;

public class CrawlWork extends AbstractJob {

	private static Logger logger = LoggerFactory.getLogger(CrawlWork.class);

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		TimeStamp stamp = new TimeStamp();
		List<String> add_works = null;
		List<String> update_works = null;
		List<String> update_references = null;
		List<String> updates = null;
		Long workCount = null;
		Long entryCount = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			workCount = emc.count(Work.class);
			entryCount = emc.countEqual(Entry.class, Entry.type_FIELDNAME, Entry.TYPE_WORK);
			add_works = this.listAddWork(business);
			update_works = this.listUpdateWork(business);
			update_references = this.listUpdateEntryReference(business);
			updates = ListUtils.sum(ListUtils.sum(add_works, update_works), update_references);
		} catch (Exception e) {
			logger.error(e);
			throw new JobExecutionException(e);
		}
		this.update(updates);
		logger.print("工作索引器运行完成, 工作总数:{}, 新索引工作数量:{}, 轮询更新工作数量:{}, 已索引条目总数:{}, 已索引条目更新数量:{}, 合并更新数量:{}, 数量限制:{}, 耗时{}.",
				workCount, add_works.size(), update_works.size(), entryCount, update_references.size(), updates.size(),
				Config.query().getCrawlWork().getCount(), stamp.consumingMilliseconds());
	}

	private void update(List<String> references) throws Exception {
		for (String reference : references) {
			try {
				ThisApplication.context().applications().getQuery(x_query_service_processing.class,
						Applications.joinQueryUri("segment", "crawl", "work", reference), reference);
			} catch (Exception e) {
				logger.error(e);
			}
		}
	}

	/**
	 * 新增数据
	 */
	private List<String> listAddWork(Business business) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Work> root = cq.from(Work.class);
		cq.select(root.get(Work_.id)).orderBy(cb.desc(root.get(Work_.sequence)));
		List<String> os = em.createQuery(cq).setMaxResults(Config.query().getCrawlWork().getCount()).getResultList();
		return os;
	}

	/**
	 * 定时进行轮询保证旧数据能定期进行更新
	 */
	private List<String> listUpdateWork(Business business) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.conjunction();
		String sequence = Arguments.getCrawlUpdateWork();
		if (StringUtils.isNotEmpty(sequence)) {
			p = cb.and(cb.lessThan(root.get(Work.sequence_FIELDNAME), sequence));
		}
		cq.select(root.get(Work_.id)).where(p).orderBy(cb.desc(root.get(Work_.sequence)));
		Integer count = Config.query().getCrawlWork().getCount() / 2;
		List<String> os = em.createQuery(cq).setMaxResults(count).getResultList();
		if (os.size() == count) {
			Arguments.setCrawlUpdateWork(os.get(os.size() - 1));
		} else {
			Arguments.setCrawlUpdateWork("");
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
		Predicate p = cb.equal(root.get(Entry_.type), Entry.TYPE_WORK);
		cq.select(root.get(Entry_.reference)).where(p).orderBy(cb.asc(root.get(Entry_.sequence)));
		Integer count = Config.query().getCrawlWork().getCount() / 2;
		List<String> os = em.createQuery(cq).setMaxResults(count).getResultList();
		return os;
	}

}