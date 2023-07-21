package com.x.organization.assemble.personal.jaxrs.regist;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Person_;

abstract class BaseAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(BaseAction.class);

	Boolean mobileExisted(EntityManagerContainer emc, String mobile) throws Exception {
		EntityManager em = emc.get(Person.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Person> root = cq.from(Person.class);
		Predicate p = cb.equal(root.get(Person_.mobile), mobile);
		p = cb.or(p, cb.equal(root.get(Person_.name), mobile));
		p = cb.or(p, cb.equal(root.get(Person_.id), mobile));
		cq.select(cb.count(root.get(Person_.id))).where(p);
		if (em.createQuery(cq).getSingleResult() > 0) {
			return true;
		} else {
			return false;
		}
	}

	Boolean nameExisted(EntityManagerContainer emc, String name) throws Exception {
		EntityManager em = emc.get(Person.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Person> root = cq.from(Person.class);
		Predicate p = cb.equal(root.get(Person_.name), name);
		p = cb.or(p, cb.equal(root.get(Person_.mobile), name));
		p = cb.or(p, cb.equal(root.get(Person_.id), name));
		cq.select(cb.count(root.get(Person_.id))).where(p);
		if (em.createQuery(cq).getSingleResult() > 0) {
			return true;
		} else {
			return false;
		}
	}

	Boolean mailExisted(EntityManagerContainer emc, String mail) throws Exception {
		EntityManager em = emc.get(Person.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Person> root = cq.from(Person.class);
		Predicate p = cb.equal(root.get(Person_.mail), mail);
		cq.select(cb.count(root.get(Person_.id))).where(p);
		if (em.createQuery(cq).getSingleResult() > 0) {
			return true;
		} else {
			return false;
		}
	}
}
