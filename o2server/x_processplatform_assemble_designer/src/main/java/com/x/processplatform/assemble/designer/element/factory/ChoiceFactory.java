package com.x.processplatform.assemble.designer.element.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.processplatform.assemble.designer.AbstractFactory;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Choice;
import com.x.processplatform.core.entity.element.Choice_;

public class ChoiceFactory extends AbstractFactory {

	public ChoiceFactory(Business business) throws Exception {
		super(business);
	}

	public List<String> listWithProcess(String processId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Choice.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Choice> root = cq.from(Choice.class);
		Predicate p = cb.equal(root.get(Choice_.process), processId);
		cq.select(root.get(Choice_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<Choice> listWithProcessObject(String processId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Choice.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Choice> cq = cb.createQuery(Choice.class);
		Root<Choice> root = cq.from(Choice.class);
		Predicate p = cb.equal(root.get(Choice_.process), processId);
		cq.select(root).where(p);
		return em.createQuery(cq).getResultList();
	}

	/** 查找使用表单的choice */
	public List<String> listWithForm(String formId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Choice.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Choice> root = cq.from(Choice.class);
		Predicate p = cb.equal(root.get(Choice_.form), formId);
		cq.select(root.get(Choice_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}
}