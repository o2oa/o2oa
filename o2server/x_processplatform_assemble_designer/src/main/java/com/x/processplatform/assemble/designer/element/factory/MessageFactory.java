package com.x.processplatform.assemble.designer.element.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.processplatform.assemble.designer.AbstractFactory;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Message;
import com.x.processplatform.core.entity.element.Message_;

public class MessageFactory extends AbstractFactory {

	public MessageFactory(Business business) throws Exception {
		super(business);
	}

	public List<String> listWithProcess(String processId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Message.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Message> root = cq.from(Message.class);
		Predicate p = cb.equal(root.get(Message_.process), processId);
		cq.select(root.get(Message_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<Message> listWithProcessObject(String processId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Message.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Message> cq = cb.createQuery(Message.class);
		Root<Message> root = cq.from(Message.class);
		Predicate p = cb.equal(root.get(Message_.process), processId);
		cq.select(root).where(p);
		return em.createQuery(cq).getResultList();
	}

	/** 查找使用表单的message */
	public List<String> listWithForm(String formId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Message.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Message> root = cq.from(Message.class);
		Predicate p = cb.equal(root.get(Message_.form), formId);
		cq.select(root.get(Message_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}
}