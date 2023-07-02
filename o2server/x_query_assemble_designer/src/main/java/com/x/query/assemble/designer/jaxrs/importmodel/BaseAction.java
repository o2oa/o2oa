package com.x.query.assemble.designer.jaxrs.importmodel;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.ImportModel;
import com.x.query.core.entity.ImportModel_;

abstract class BaseAction extends StandardJaxrsAction {

	protected boolean idleName(Business business, ImportModel model) throws Exception {
		EntityManager em = business.entityManagerContainer().get(ImportModel.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<ImportModel> root = cq.from(ImportModel.class);
		Predicate p = cb.equal(root.get(ImportModel_.query), model.getQuery());
		p = cb.and(p, cb.equal(root.get(ImportModel_.name), model.getName()));
		p = cb.and(p, cb.notEqual(root.get(ImportModel_.id), model.getId()));
		return em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult() == 0;
	}

	protected boolean idleAlias(Business business, ImportModel model) throws Exception {
		EntityManager em = business.entityManagerContainer().get(ImportModel.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<ImportModel> root = cq.from(ImportModel.class);
		Predicate p = cb.equal(root.get(ImportModel_.query), model.getQuery());
		p = cb.and(p, cb.equal(root.get(ImportModel_.alias), model.getAlias()));
		p = cb.and(p, cb.notEqual(root.get(ImportModel_.id), model.getId()));
		return em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult() == 0;
	}

}
