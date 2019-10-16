package com.x.query.service.processing.schedule;

import java.util.ArrayList;
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

import com.hankcs.hanlp.HanLP;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.base.core.project.utils.time.TimeStamp;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.query.core.entity.Item;
import com.x.query.core.entity.segment.Entry;
import com.x.query.core.entity.segment.Entry_;
import com.x.query.core.entity.segment.Word;
import com.x.query.service.processing.Business;
import com.x.query.service.processing.helper.LanguageProcessingHelper;

public class CrawlWorkCompleted extends Crawl {

	private static Logger logger = LoggerFactory.getLogger(CrawlWorkCompleted.class);

	private static DataItemConverter<Item> converter;

	private static final Integer BATCH_SIZE = 500;

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		TimeStamp stamp = new TimeStamp();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			converter = new DataItemConverter<Item>(Item.class);
			Business business = new Business(emc);
			List<String> entry_references = this.listEntryReference(business);
			List<String> ids = business.entityManagerContainer().ids(WorkCompleted.class);
			List<String> add_references = ListUtils.subtract(ids, entry_references);
			List<String> update_references = this.listOldToUpdateReference(business);
			List<String> remove_references = ListUtils.subtract(entry_references, ids);
			update_references = ListUtils.subtract(update_references, remove_references);
			this.update(business, update_references);
			this.remove(business, remove_references);
			this.add(business, add_references);
			emc.flush();
			this.crawl(business);
			logger.print("已完成工作索引器条目总数:{}, 工作总数:{}, 需要新增:{}, 需要删除:{}, 更新数量:{}, 数量限制:{}, 耗时{}.", entry_references.size(),
					ids.size(), add_references.size(), remove_references.size(), update_references.size(),
					Config.query().getCrawlWorkCompleted().getCount(), stamp.consumingMilliseconds());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new JobExecutionException(e);
		}
	}

	private List<String> listEntryReference(Business business) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Entry.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Entry> root = cq.from(Entry.class);
		Predicate p = cb.equal(root.get(Entry_.type), Entry.TYPE_WORKCOMPLETED);
		cq.select(root.get(Entry_.reference)).where(p);
		List<String> os = em.createQuery(cq).getResultList();
		return os;
	}

	private void remove(Business business, List<String> references) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		for (String reference : references) {
			emc.beginTransaction(Entry.class);
			emc.beginTransaction(Word.class);
			for (Entry en : emc.listEqualAndEqual(Entry.class, Entry.type_FIELDNAME, Entry.TYPE_WORKCOMPLETED,
					Entry.reference_FIELDNAME, reference)) {
				for (Word w : emc.listEqual(Word.class, Word.entry_FIELDNAME, en.getId())) {
					emc.remove(w);
				}
				emc.remove(en);
			}
			emc.commit();
		}
	}

	private void update(Business business, List<String> references) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		for (List<String> list : ListTools.batch(references, BATCH_SIZE)) {
			emc.beginTransaction(Entry.class);
			for (Entry o : emc.listEqualAndIn(Entry.class, Entry.type_FIELDNAME, Entry.TYPE_WORKCOMPLETED,
					Entry.reference_FIELDNAME, list)) {
				o.setWait(true);
			}
			emc.commit();
		}
	}

	private void add(Business business, List<String> references) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		if (references.size() > Config.query().getCrawlWorkCompleted().getCount()) {
			references = references.subList(0, Config.query().getCrawlWorkCompleted().getCount());
		}
		for (List<String> list : ListTools.batch(references, 500)) {
			emc.beginTransaction(Entry.class);
			for (WorkCompleted workCompleted : emc.list(WorkCompleted.class, list)) {
				Entry entry = new Entry();
				entry.setLastUpdateTime(workCompleted.getUpdateTime());
				entry.setType(Entry.TYPE_WORKCOMPLETED);
				entry.setReference(workCompleted.getId());
				entry.setBundle(workCompleted.getJob());
				entry.setWait(true);
				emc.persist(entry, CheckPersistType.all);
			}
			emc.commit();
		}
	}

	private void crawl(Business business) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		for (Entry entry : this.listWait(business)) {
			WorkCompleted workCompleted = emc.find(entry.getReference(), WorkCompleted.class);
			if (null != workCompleted) {
				logger.debug("正在处理:{}.", workCompleted.getTitle());
				entry.setTitle(workCompleted.getTitle());
				entry.setBundle(workCompleted.getJob());
				entry.setApplication(workCompleted.getApplication());
				entry.setApplicationName(workCompleted.getApplicationName());
				entry.setProcess(workCompleted.getProcess());
				entry.setProcessName(workCompleted.getProcessName());
				entry.setCreatorPerson(workCompleted.getCreatorPerson());
				entry.setCreatorUnit(workCompleted.getCreatorUnit());
				emc.beginTransaction(Entry.class);
				emc.beginTransaction(Word.class);
				emc.deleteEqual(Word.class, Word.entry_FIELDNAME, entry.getId());
				String title = workCompleted.getTitle();
				String body = converter.text(emc.listEqualAndEqual(Item.class, Item.itemCategory_FIELDNAME,
						ItemCategory.pp, Item.bundle_FIELDNAME, workCompleted.getJob()), true, true, true, true, true,
						",");
				String attachment = this.attachmentToText(business, workCompleted);
				title = StringUtils.deleteWhitespace(title);
				body = StringUtils.deleteWhitespace(body);
				attachment = StringUtils.deleteWhitespace(attachment);
				String text = body + attachment;
				String summary = StringUtils.join(HanLP.extractSummary(text, 10), ",");
				entry.setSummary(StringTools.utf8SubString(summary, JpaObject.length_255B));
				Word word = null;
				if (StringUtils.isNotEmpty(title)) {
					for (LanguageProcessingHelper.Item o : this.toWord(title)) {
						if (StringUtils.length(o.getValue()) < 31) {
							/* 可能产生过长的字比如...................................... */
							word = new Word();
							word.setEntry(entry.getId());
							word.setBundle(entry.getBundle());
							word.setType(entry.getType());
							word.setValue(o.getValue());
							word.setLabel(o.getLabel());
							word.setTag(Word.TAG_TITLE);
							word.setCount(o.getCount().intValue());
							emc.persist(word, CheckPersistType.all);
						}
					}
				}
				if (StringUtils.isNotEmpty(body)) {
					for (LanguageProcessingHelper.Item o : this.toWord(body)) {
						if (StringUtils.length(o.getValue()) < 31) {
							/* 可能产生过长的字比如...................................... */
							word = new Word();
							word.setEntry(entry.getId());
							word.setBundle(entry.getBundle());
							word.setType(entry.getType());
							word.setValue(o.getValue());
							word.setLabel(o.getLabel());
							word.setTag(Word.TAG_BODY);
							word.setCount(o.getCount().intValue());
							emc.persist(word, CheckPersistType.all);
						}
					}
				}
				if (StringUtils.isNotEmpty(attachment)) {
					for (LanguageProcessingHelper.Item o : this.toWord(attachment)) {
						if (StringUtils.length(o.getValue()) < 31) {
							/* 可能产生过长的字比如...................................... */
							word = new Word();
							word.setEntry(entry.getId());
							word.setBundle(entry.getBundle());
							word.setType(entry.getType());
							word.setValue(o.getValue());
							word.setLabel(o.getLabel());
							word.setTag(Word.TAG_ATTACHMENT);
							word.setCount(o.getCount().intValue());
							emc.persist(word, CheckPersistType.all);
						}
					}
				}
				entry.setWait(false);
				emc.commit();
			}
		}
	}

	private List<Entry> listWait(Business business) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Entry.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Entry> cq = cb.createQuery(Entry.class);
		Root<Entry> root = cq.from(Entry.class);
		Predicate p = cb.or(cb.isNull(root.get(Entry_.wait)), cb.equal(root.get(Entry_.wait), true));
		p = cb.and(p, cb.equal(root.get(Entry_.type), Entry.TYPE_WORKCOMPLETED));
		cq.select(root).where(p);
		List<Entry> os = em.createQuery(cq).setMaxResults(Config.query().getCrawlWorkCompleted().getCount())
				.getResultList();
		return os;
	}

	private List<String> listOldToUpdateReference(Business business) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Entry.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Entry> cq = cb.createQuery(Entry.class);
		Root<Entry> root = cq.from(Entry.class);
		Predicate p = cb.or(cb.isNull(root.get(Entry_.wait)), cb.equal(root.get(Entry_.wait), false));
		p = cb.and(p, cb.equal(root.get(Entry_.type), Entry.TYPE_WORKCOMPLETED));
		cq.select(root).where(p).orderBy(cb.asc(root.get(Entry_.updateTime)));
		List<Entry> os = em.createQuery(cq).setMaxResults(Config.query().getCrawlWorkCompleted().getCount() / 7)
				.getResultList();
		List<String> list = new ArrayList<>();
		for (Entry entry : os) {
			list.add(entry.getReference());
		}
		return list;
	}

	private String attachmentToText(Business business, WorkCompleted workCompleted) throws Exception {
		StringBuffer buffer = new StringBuffer();
		EntityManagerContainer emc = business.entityManagerContainer();
		for (Attachment o : emc.listEqual(Attachment.class, WorkCompleted.job_FIELDNAME, workCompleted.getJob())) {
			if (StringUtils.isNotEmpty(o.getText())) {
				buffer.append(o.getText());
			} else {
				buffer.append(this.text(o));
			}
		}
		return buffer.toString();
	}

}