package com.x.processplatform.service.processing.trigger.task;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkStatus;
import com.x.processplatform.core.entity.content.Work_;
import com.x.processplatform.core.entity.temporary.TriggerWorkRecord;

public class TriggerWorkTask implements Runnable{

	@Deprecated
	public void run() {
		try {
			List<String> works = new ArrayList<>();
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				EntityManager em = emc.get(TriggerWorkRecord.class);
				emc.beginTransaction(TriggerWorkRecord.class);
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<TriggerWorkRecord> cq = cb.createQuery(TriggerWorkRecord.class);
				Root<TriggerWorkRecord> root = cq.from(TriggerWorkRecord.class);
				cq.select(root);
				List<TriggerWorkRecord> list = em.createQuery(cq).setLockMode(LockModeType.PESSIMISTIC_READ)
						.getResultList();
				TriggerWorkRecord record = null;
				if (!list.isEmpty()) {
					record = list.get(0);
				} else {
					record = new TriggerWorkRecord();
					em.persist(record);
				}
				String lastSequence = record.getLastSequence();
				works = this.listWorkAfterSequence(lastSequence);
				if (works.isEmpty()) {
					record.setSequence("");
				} else {
					record.setSequence(works.get(works.size() - 1));
				}
				emc.commit();
			}
			for (String str : works) {
				// ThisApplication.applications.putQuery(x_processplatform_service_processing.class,
				// "work/" + URLEncoder.encode(str, "UTF-8"), null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private List<String> listWorkAfterSequence(String sequence) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			List<String> list = new ArrayList<>();
			EntityManager em = emc.get(Work.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Work> cq = cb.createQuery(Work.class);
			Root<Work> root = cq.from(Work.class);
			Predicate p = cb.equal(root.get(Work_.workStatus), WorkStatus.processing);
			if (StringUtils.isNotEmpty(sequence)) {
				p = cb.and(p, cb.greaterThan(root.get(Work_.sequence), sequence));
			}
			cq.where(p).orderBy(cb.desc(root.get(Work_.sequence)));
			for (Work o : em.createQuery(cq).setMaxResults(200).getResultList()) {
				list.add(o.getId());
			}
			return list;
		}
	}
}