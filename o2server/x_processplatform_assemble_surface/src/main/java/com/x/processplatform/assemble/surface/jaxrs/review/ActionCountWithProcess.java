package com.x.processplatform.assemble.surface.jaxrs.review;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.bean.NameValueCountPair;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.SortTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Review_;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;

public class ActionCountWithProcess extends BaseAction {

	ActionResult<List<NameValueCountPair>> execute(EffectivePerson effectivePerson, String applicationFlag)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<NameValueCountPair>> result = new ActionResult<>();
			List<NameValueCountPair> wraps = new ArrayList<>();
			Business business = new Business(emc);
			Application application = business.application().pick(applicationFlag);
			if (null == application) {
				throw new ExceptionEntityNotExist(applicationFlag, Application.class);
			}
			EntityManager em = emc.get(Review.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<String> cq = cb.createQuery(String.class);
			Root<Review> root = cq.from(Review.class);
			Predicate p = cb.equal(root.get(Review_.person), effectivePerson.getDistinguishedName());
			p = cb.and(p, cb.equal(root.get(Review_.application), application.getId()));
			cq.select(root.get(Review_.process)).where(p).distinct(true);
			List<String> list = em.createQuery(cq).getResultList();
			for (String str : list) {
				NameValueCountPair o = this.concreteNameValueCountPair(business, effectivePerson, str);
				wraps.add(o);
			}
			SortTools.asc(wraps, false, "name");
			return result;
		}
	}

	private NameValueCountPair concreteNameValueCountPair(Business business, EffectivePerson effectivePerson,
			String processId) throws Exception {
		NameValueCountPair pair = new NameValueCountPair();
		pair.setValue(processId);
		pair.setName(this.getProcessName(business, effectivePerson, processId));
		pair.setCount(this.count(business, effectivePerson, processId));
		return pair;
	}

	private String getProcessName(Business business, EffectivePerson effectivePerson, String processId)
			throws Exception {
		Process process = business.process().pick(processId);
		if (null != process) {
			return process.getName();
		} else {
			EntityManagerContainer emc = business.entityManagerContainer();
			EntityManager em = emc.get(Review.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<String> cq = cb.createQuery(String.class);
			Root<Review> root = cq.from(Review.class);
			Predicate p = cb.equal(root.get(Review_.person), effectivePerson.getDistinguishedName());
			p = cb.and(p, cb.equal(root.get(Review_.process), processId));
			cq.select(root.get(Review_.processName)).where(p);
			List<String> list = em.createQuery(cq).setMaxResults(1).getResultList();
			if (!list.isEmpty()) {
				return list.get(0);
			}
			return null;
		}
	}

	private Long count(Business business, EffectivePerson effectivePerson, String processId) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(Review.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Review> root = cq.from(Review.class);
		Predicate p = cb.equal(root.get(Review_.person), effectivePerson.getDistinguishedName());
		p = cb.and(p, cb.equal(root.get(Review_.process), processId));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}
}
