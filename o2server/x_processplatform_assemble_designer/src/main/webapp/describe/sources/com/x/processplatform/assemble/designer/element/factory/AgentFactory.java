package com.x.processplatform.assemble.designer.element.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.processplatform.assemble.designer.AbstractFactory;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Agent;
import com.x.processplatform.core.entity.element.Agent_;

public class AgentFactory extends AbstractFactory {

	public AgentFactory(Business business) throws Exception {
		super(business);
	}

	public List<String> listWithProcess(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Agent.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Agent> root = cq.from(Agent.class);
		Predicate p = cb.equal(root.get(Agent_.process), id);
		cq.select(root.get(Agent_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<Agent> listWithProcessObject(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Agent.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Agent> cq = cb.createQuery(Agent.class);
		Root<Agent> root = cq.from(Agent.class);
		Predicate p = cb.equal(root.get(Agent_.process), id);
		cq.select(root).where(p);
		return em.createQuery(cq).getResultList();
	}

	/** 查找使用表单的agent */
	public List<String> listWithForm(String formId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Agent.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Agent> root = cq.from(Agent.class);
		Predicate p = cb.equal(root.get(Agent_.form), formId);
		cq.select(root.get(Agent_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

}