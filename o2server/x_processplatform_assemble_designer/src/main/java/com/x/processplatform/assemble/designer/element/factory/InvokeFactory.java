package com.x.processplatform.assemble.designer.element.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.processplatform.assemble.designer.AbstractFactory;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Invoke;
import com.x.processplatform.core.entity.element.Invoke_;

public class InvokeFactory extends AbstractFactory {

	public InvokeFactory(Business business) throws Exception {
		super(business);
	}

	public List<String> listWithProcess(String processId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Invoke.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Invoke> root = cq.from(Invoke.class);
		Predicate p = cb.equal(root.get(Invoke_.process), processId);
		cq.select(root.get(Invoke_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<Invoke> listWithProcessObject(String processId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Invoke.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Invoke> cq = cb.createQuery(Invoke.class);
		Root<Invoke> root = cq.from(Invoke.class);
		Predicate p = cb.equal(root.get(Invoke_.process), processId);
		cq.select(root).where(p);
		return em.createQuery(cq).getResultList();
	}

	/** 查找使用表单的invoke */
	public List<String> listWithForm(String formId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Invoke.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Invoke> root = cq.from(Invoke.class);
		Predicate p = cb.equal(root.get(Invoke_.form), formId);
		cq.select(root.get(Invoke_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}
}