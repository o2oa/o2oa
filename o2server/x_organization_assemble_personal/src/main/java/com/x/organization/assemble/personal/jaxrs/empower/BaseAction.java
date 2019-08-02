package com.x.organization.assemble.personal.jaxrs.empower;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.organization.assemble.personal.Business;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.accredit.Empower;
import com.x.organization.core.entity.accredit.Empower_;

abstract class BaseAction extends StandardJaxrsAction {

	protected boolean checkWhole(Business business, Empower empower) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Empower.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Empower> root = cq.from(Empower.class);
		Predicate p = cb.equal(root.get(Empower_.fromIdentity), empower.getFromIdentity());
		p = cb.and(p, cb.equal(root.get(Empower_.whole), true));
		p = cb.and(p, cb.notEqual(root.get(Empower_.id), empower.getId()));
		Long count = em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult();
		if (count > 0) {
			return false;
		} else {
			return true;
		}
	}

	protected boolean checkApplication(Business business, Empower empower) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Empower.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Empower> root = cq.from(Empower.class);
		Predicate p = cb.equal(root.get(Empower_.fromIdentity), empower.getFromIdentity());
		p = cb.and(p, cb.equal(root.get(Empower_.application), empower.getApplication()));
		p = cb.and(p, cb.or(cb.equal(root.get(Empower_.process), ""), cb.isNull(root.get(Empower_.process))));
		p = cb.and(p, cb.notEqual(root.get(Empower_.id), empower.getId()));
		Long count = em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult();
		if (count > 0) {
			return false;
		} else {
			return true;
		}
	}

	protected boolean checkProcess(Business business, Empower empower) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Empower.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Empower> root = cq.from(Empower.class);
		Predicate p = cb.equal(root.get(Empower_.fromIdentity), empower.getFromIdentity());
		p = cb.and(p, cb.equal(root.get(Empower_.application), empower.getApplication()));
		p = cb.and(p, cb.equal(root.get(Empower_.process), empower.getProcess()));
		p = cb.and(p, cb.notEqual(root.get(Empower_.id), empower.getId()));
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