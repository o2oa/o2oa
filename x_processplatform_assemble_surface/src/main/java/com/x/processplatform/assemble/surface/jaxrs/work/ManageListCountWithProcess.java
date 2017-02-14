package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.bean.NameValueCountPair;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.utils.SortTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.Work_;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;

class ManageListCountWithProcess {

	ActionResult<List<NameValueCountPair>> execute(EffectivePerson effectivePerson, String applicationFlag)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<NameValueCountPair>> result = new ActionResult<>();
			Business business = new Business(emc);
			List<NameValueCountPair> wraps = new ArrayList<>();
			Application application = business.application().pick(applicationFlag, ExceptionWhen.not_found);
			EntityManager em = business.entityManagerContainer().get(Work.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<String> cq = cb.createQuery(String.class);
			Root<Work> root = cq.from(Work.class);
			Predicate p = cb.equal(root.get(Work_.application), application.getId());
			cq.select(root.get(Work_.process)).where(p).distinct(true);
			List<String> list = em.createQuery(cq).getResultList();
			for (String str : list) {
				NameValueCountPair o = new NameValueCountPair();
				o.setValue(str);
				o.setName(this.getProcessName(business, str));
				o.setCount(this.countWithProcess(business, str));
				wraps.add(o);
			}
			SortTools.asc(wraps, "name");
			result.setData(wraps);
			return result;
		}
	}

	private String getProcessName(Business business, String id) throws Exception {
		Process o = business.process().pick(id);
		if (null != o) {
			return o.getName();
		}
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.equal(root.get(Work_.process), id);
		cq.select(root.get(Work_.processName)).where(p);
		List<String> list = em.createQuery(cq).setMaxResults(1).getResultList();
		if (!list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	private Long countWithProcess(Business business, String process) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.equal(root.get(Work_.process), process);
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

}