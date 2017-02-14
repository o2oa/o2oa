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

	public List<String> listWithProcess(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Message.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Message> root = cq.from(Message.class);
		Predicate p = cb.equal(root.get(Message_.process), id);
		cq.select(root.get(Message_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}
}