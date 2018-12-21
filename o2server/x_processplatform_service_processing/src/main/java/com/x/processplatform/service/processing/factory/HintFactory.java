package com.x.processplatform.service.processing.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.processplatform.core.entity.content.Hint;
import com.x.processplatform.core.entity.content.Hint_;
import com.x.processplatform.service.processing.AbstractFactory;
import com.x.processplatform.service.processing.Business;

public class HintFactory extends AbstractFactory {

	public HintFactory(Business business) throws Exception {
		super(business);
	}

	public List<String> listWithWork(String workId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Hint.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Hint> root = cq.from(Hint.class);
		Predicate p = cb.equal(root.get(Hint_.work), workId);
		cq.select(root.get(Hint_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithJob(String job) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Hint.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Hint> root = cq.from(Hint.class);
		Predicate p = cb.equal(root.get(Hint_.job), job);
		cq.select(root.get(Hint_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}
}