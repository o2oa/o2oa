package com.x.query.assemble.designer.jaxrs.stat;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.Stat;
import com.x.query.core.entity.Stat_;

abstract class BaseAction extends StandardJaxrsAction {

	protected boolean idleName(Business business, Stat stat) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Stat.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Stat> root = cq.from(Stat.class);
		Predicate p = cb.equal(root.get(Stat_.query), stat.getQuery());
		p = cb.and(p, cb.equal(root.get(Stat_.name), stat.getName()));
		p = cb.and(p, cb.notEqual(root.get(Stat_.id), stat.getId()));
		cq.select(cb.count(root)).where(p);
		Long count = em.createQuery(cq).getSingleResult();
		return count == 0L;
	}

	protected boolean idleAlias(Business business, Stat stat) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Stat.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Stat> root = cq.from(Stat.class);
		Predicate p = cb.equal(root.get(Stat_.query), stat.getQuery());
		p = cb.and(p, cb.equal(root.get(Stat_.alias), stat.getAlias()));
		p = cb.and(p, cb.notEqual(root.get(Stat_.id), stat.getId()));
		return em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult() == 0;
	}
}
