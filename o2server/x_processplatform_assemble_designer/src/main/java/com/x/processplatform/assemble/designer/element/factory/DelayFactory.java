package com.x.processplatform.assemble.designer.element.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.processplatform.assemble.designer.AbstractFactory;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Delay;
import com.x.processplatform.core.entity.element.Delay_;

public class DelayFactory extends AbstractFactory {

	public DelayFactory(Business business) throws Exception {
		super(business);
	}

	public List<String> listWithProcess(String processId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Delay.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Delay> root = cq.from(Delay.class);
		Predicate p = cb.equal(root.get(Delay_.process), processId);
		cq.select(root.get(Delay_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<Delay> listWithProcessObject(String processId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Delay.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Delay> cq = cb.createQuery(Delay.class);
		Root<Delay> root = cq.from(Delay.class);
		Predicate p = cb.equal(root.get(Delay_.process), processId);
		cq.select(root).where(p);
		return em.createQuery(cq).getResultList();
	}

	/** 查找使用表单的delay */
	public List<String> listWithForm(String formId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Delay.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Delay> root = cq.from(Delay.class);
		Predicate p = cb.equal(root.get(Delay_.form), formId);
		cq.select(root.get(Delay_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}
}