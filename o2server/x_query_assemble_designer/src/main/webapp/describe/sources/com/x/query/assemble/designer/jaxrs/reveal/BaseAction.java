package com.x.query.assemble.designer.jaxrs.reveal;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.Reveal;
import com.x.query.core.entity.Reveal_;

abstract class BaseAction extends StandardJaxrsAction {

	protected boolean idleName(Business business, Reveal view) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Reveal.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Reveal> root = cq.from(Reveal.class);
		Predicate p = cb.equal(root.get(Reveal_.query), view.getQuery());
		p = cb.and(p, cb.equal(root.get(Reveal_.name), view.getName()));
		p = cb.and(p, cb.notEqual(root.get(Reveal_.id), view.getId()));
		return em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult() == 0;
	}

	protected boolean idleAlias(Business business, Reveal view) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Reveal.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Reveal> root = cq.from(Reveal.class);
		Predicate p = cb.equal(root.get(Reveal_.query), view.getQuery());
		p = cb.and(p, cb.equal(root.get(Reveal_.alias), view.getAlias()));
		p = cb.and(p, cb.notEqual(root.get(Reveal_.id), view.getId()));
		return em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult() == 0;
	}
}
