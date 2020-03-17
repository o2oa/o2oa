package com.x.processplatform.assemble.designer.element.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.processplatform.assemble.designer.AbstractFactory;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Begin;
import com.x.processplatform.core.entity.element.Begin_;

public class BeginFactory extends AbstractFactory {

	public BeginFactory(Business business) throws Exception {
		super(business);
	}

	public String getWithProcess(String processId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Begin.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Begin> root = cq.from(Begin.class);
		Predicate p = cb.equal(root.get(Begin_.process), processId);
		cq.select(root.get(Begin_.id)).where(p);
		List<String> list = em.createQuery(cq).setMaxResults(1).getResultList();
		return list.isEmpty() ? null : list.get(0);
	}

	public Begin getWithProcessObject(String processId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Begin.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Begin> cq = cb.createQuery(Begin.class);
		Root<Begin> root = cq.from(Begin.class);
		Predicate p = cb.equal(root.get(Begin_.process), processId);
		cq.select(root).where(p);
		List<Begin> list = em.createQuery(cq).setMaxResults(1).getResultList();
		return list.isEmpty() ? null : list.get(0);
	}

	/** 查找使用表单的begin */
	public List<String> listWithForm(String formId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Begin.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Begin> root = cq.from(Begin.class);
		Predicate p = cb.equal(root.get(Begin_.form), formId);
		cq.select(root.get(Begin_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

}