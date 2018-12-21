package com.x.processplatform.assemble.surface.jaxrs.taskcompleted;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.TaskCompleted_;

abstract class BaseAction extends StandardJaxrsAction {

	protected Long countWithApplication(Business business, EffectivePerson effectivePerson, String id)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.equal(root.get(TaskCompleted_.person), effectivePerson.getDistinguishedName());
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.application), id));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}


//	protected Long countWithProcess(Business business, EffectivePerson effectivePerson, String id) throws Exception {
//		EntityManager em = business.entityManagerContainer().get(TaskCompleted.class);
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
//		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
//		Predicate p = cb.equal(root.get(TaskCompleted_.person), effectivePerson.getDistinguishedName());
//		p = cb.and(p, cb.equal(root.get(TaskCompleted_.process), id));
//		cq.select(cb.count(root)).where(p);
//		return em.createQuery(cq).getSingleResult();
//	}

}