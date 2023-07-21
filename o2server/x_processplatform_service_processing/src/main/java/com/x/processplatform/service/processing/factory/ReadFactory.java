package com.x.processplatform.service.processing.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.Read_;
import com.x.processplatform.service.processing.AbstractFactory;
import com.x.processplatform.service.processing.Business;

public class ReadFactory extends AbstractFactory {

	public ReadFactory(Business business) throws Exception {
		super(business);
	}

	public List<String> listWithWork(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Read.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Read> root = cq.from(Read.class);
		Predicate p = cb.equal(root.get(Read_.work), id);
		cq.select(root.get(Read_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

//	public List<String> listWithJob(String job) throws Exception {
//		EntityManager em = this.entityManagerContainer().get(Read.class);
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<String> cq = cb.createQuery(String.class);
//		Root<Read> root = cq.from(Read.class);
//		Predicate p = cb.equal(root.get(Read_.job), job);
//		cq.select(root.get(Read_.id)).where(p);
//		return em.createQuery(cq).getResultList();
//	}

//	public List<String> listWithPersonWithWork(String person, String work) throws Exception {
//		EntityManager em = this.entityManagerContainer().get(Read.class);
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<String> cq = cb.createQuery(String.class);
//		Root<Read> root = cq.from(Read.class);
//		Predicate p = cb.equal(root.get(Read_.person), person);
//		p = cb.and(p, cb.equal(root.get(Read_.completed), false));
//		p = cb.and(p, cb.equal(root.get(Read_.work), work));
//		cq.select(root.get(Read_.id)).where(p);
//		return em.createQuery(cq).getResultList();
//	}

//	public List<String> listWithPersonWithWorkCompleted(String person, String workCompleted) throws Exception {
//		EntityManager em = this.entityManagerContainer().get(Read.class);
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<String> cq = cb.createQuery(String.class);
//		Root<Read> root = cq.from(Read.class);
//		Predicate p = cb.equal(root.get(Read_.person), person);
//		p = cb.and(p, cb.equal(root.get(Read_.completed), true));
//		p = cb.and(p, cb.equal(root.get(Read_.workCompleted), workCompleted));
//		cq.select(root.get(Read_.id)).where(p);
//		return em.createQuery(cq).getResultList();
//	}

//	public List<String> listWithActivityToken(String activityToken) throws Exception {
//		EntityManager em = this.entityManagerContainer().get(Read.class);
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<String> cq = cb.createQuery(String.class);
//		Root<Read> root = cq.from(Read.class);
//		Predicate p = cb.equal(root.get(Read_.activityToken), activityToken);
//		cq.select(root.get(Read_.id)).where(p);
//		return em.createQuery(cq).getResultList();
//	}
}