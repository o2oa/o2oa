package com.x.organization.assemble.personal.jaxrs.trust;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.organization.assemble.personal.Business;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.accredit.Trust;
import com.x.organization.core.entity.accredit.Trust_;

abstract class BaseAction extends StandardJaxrsAction {

	protected boolean checkWhole(Business business, Trust trust) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Trust.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Trust> root = cq.from(Trust.class);
		Predicate p = cb.equal(root.get(Trust_.fromIdentity), trust.getFromIdentity());
		p = cb.and(p, cb.equal(root.get(Trust_.whole), true));
		p = cb.and(p, cb.notEqual(root.get(Trust_.id), trust.getId()));
		Long count = em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult();
		if (count > 0) {
			return false;
		} else {
			return true;
		}
	}

	protected boolean checkApplication(Business business, Trust trust) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Trust.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Trust> root = cq.from(Trust.class);
		Predicate p = cb.equal(root.get(Trust_.fromIdentity), trust.getFromIdentity());
		p = cb.and(p, cb.equal(root.get(Trust_.application), trust.getApplication()));
		p = cb.and(p, cb.or(cb.equal(root.get(Trust_.process), ""), cb.isNull(root.get(Trust_.process))));
		p = cb.and(p, cb.notEqual(root.get(Trust_.id), trust.getId()));
		Long count = em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult();
		if (count > 0) {
			return false;
		} else {
			return true;
		}
	}

	protected boolean checkProcess(Business business, Trust trust) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Trust.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Trust> root = cq.from(Trust.class);
		Predicate p = cb.equal(root.get(Trust_.fromIdentity), trust.getFromIdentity());
		p = cb.and(p, cb.equal(root.get(Trust_.application), trust.getApplication()));
		p = cb.and(p, cb.equal(root.get(Trust_.process), trust.getProcess()));
		p = cb.and(p, cb.notEqual(root.get(Trust_.id), trust.getId()));
		Long count = em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult();
		if (count > 0) {
			return false;
		} else {
			return true;
		}
	}

	protected String getPersonDNWithIdentityDN(Business business, String dn) throws Exception {
		Identity identity = business.identity().pick(dn);
		if (null != identity) {
			Person person = business.person().pick(identity.getPerson());
			if (null != person) {
				return person.getDistinguishedName();
			}
		}
		return null;
	}
}