package com.x.processplatform.assemble.designer.element.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.processplatform.assemble.designer.AbstractFactory;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Embed;
import com.x.processplatform.core.entity.element.Embed_;

public class EmbedFactory extends AbstractFactory {

	public EmbedFactory(Business business) throws Exception {
		super(business);
	}

	public List<String> listWithProcess(String processId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Embed.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Embed> root = cq.from(Embed.class);
		Predicate p = cb.equal(root.get(Embed_.process), processId);
		cq.select(root.get(Embed_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<Embed> listWithProcessObject(String processId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Embed.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Embed> cq = cb.createQuery(Embed.class);
		Root<Embed> root = cq.from(Embed.class);
		Predicate p = cb.equal(root.get(Embed_.process), processId);
		cq.select(root).where(p);
		return em.createQuery(cq).getResultList();
	}

	/** 查找使用表单的embed */
	public List<String> listWithForm(String formId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Embed.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Embed> root = cq.from(Embed.class);
		Predicate p = cb.equal(root.get(Embed_.form), formId);
		cq.select(root.get(Embed_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}
}