package com.x.processplatform.assemble.designer.element.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.processplatform.assemble.designer.AbstractFactory;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.End;
import com.x.processplatform.core.entity.element.End_;

public class EndFactory extends AbstractFactory {

	public EndFactory(Business business) throws Exception {
		super(business);
	}

	public List<String> listWithProcess(String processId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(End.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<End> root = cq.from(End.class);
		Predicate p = cb.equal(root.get(End_.process), processId);
		cq.select(root.get(End_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<End> listWithProcessObject(String processId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(End.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<End> cq = cb.createQuery(End.class);
		Root<End> root = cq.from(End.class);
		Predicate p = cb.equal(root.get(End_.process), processId);
		cq.select(root).where(p);
		return em.createQuery(cq).getResultList();
	}

	/** 查找使用表单的end */
	public List<String> listWithForm(String formId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(End.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<End> root = cq.from(End.class);
		Predicate p = cb.equal(root.get(End_.form), formId);
		cq.select(root.get(End_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}
}