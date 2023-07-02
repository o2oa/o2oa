package com.x.processplatform.assemble.designer.element.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.processplatform.assemble.designer.AbstractFactory;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Merge;
import com.x.processplatform.core.entity.element.Merge_;

public class MergeFactory extends AbstractFactory {

	public MergeFactory(Business business) throws Exception {
		super(business);
	}

	public List<String> listWithProcess(String processId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Merge.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Merge> root = cq.from(Merge.class);
		Predicate p = cb.equal(root.get(Merge_.process), processId);
		cq.select(root.get(Merge_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<Merge> listWithProcessObject(String processId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Merge.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Merge> cq = cb.createQuery(Merge.class);
		Root<Merge> root = cq.from(Merge.class);
		Predicate p = cb.equal(root.get(Merge_.process), processId);
		cq.select(root).where(p);
		return em.createQuery(cq).getResultList();
	}

	/** 查找使用表单的merge */
	public List<String> listWithForm(String formId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Merge.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Merge> root = cq.from(Merge.class);
		Predicate p = cb.equal(root.get(Merge_.form), formId);
		cq.select(root.get(Merge_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}
}