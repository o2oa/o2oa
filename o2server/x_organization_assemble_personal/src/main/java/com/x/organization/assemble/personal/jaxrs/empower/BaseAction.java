package com.x.organization.assemble.personal.jaxrs.empower;

import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.exception.ExceptionEntityFieldEmpty;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.organization.assemble.personal.Business;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.accredit.Empower;
import com.x.organization.core.entity.accredit.Empower_;

import net.sf.ehcache.Ehcache;

abstract class BaseAction extends StandardJaxrsAction {

	protected static Ehcache cache = ApplicationCache.instance().getCache(Empower.class);

	protected void check(Business business, Empower empower) throws Exception {
		if (StringUtils.isEmpty(empower.getFromIdentity())) {
			throw new ExceptionEmptyFromIdentity();
		}
		if (StringUtils.isEmpty(empower.getToIdentity())) {
			throw new ExceptionEmptyToIdentity();
		}
		switch (Objects.toString(empower.getType())) {
		case Empower.TYPE_ALL:
			if (this.typeAllExist(business, empower)) {
				throw new ExceptionTypeAllExist(empower.getFromIdentity());
			}
			break;
		case Empower.TYPE_APPLICATION:
			if (this.typeApplicationExist(business, empower)) {
				throw new ExceptionTypeApplicationExist(empower.getFromIdentity(), empower.getApplication());
			}
			break;
		case Empower.TYPE_PROCESS:
			if (this.typeProcessExist(business, empower)) {
				throw new ExceptionTypeProcessExist(empower.getFromIdentity(), empower.getProcess());
			}
			break;
		default:
			throw new ExceptionEntityFieldEmpty(Empower.class, Empower.type_FIELDNAME);
		}
	}

	private boolean typeAllExist(Business business, Empower empower) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Empower.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Empower> root = cq.from(Empower.class);
		Predicate p = cb.equal(root.get(Empower_.fromIdentity), empower.getFromIdentity());
		p = cb.and(p, cb.equal(root.get(Empower_.type), Empower.TYPE_ALL));
		p = cb.and(p, cb.notEqual(root.get(Empower_.id), empower.getId()));
		Long count = em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult();
		if (count > 0) {
			return true;
		} else {
			return false;
		}
	}

	private boolean typeApplicationExist(Business business, Empower empower) throws Exception {
		if (StringUtils.isEmpty(empower.getApplication())) {
			throw new ExceptionEntityFieldEmpty(Empower.class, Empower.application_FIELDNAME);
		}
		EntityManager em = business.entityManagerContainer().get(Empower.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Empower> root = cq.from(Empower.class);
		Predicate p = cb.equal(root.get(Empower_.fromIdentity), empower.getFromIdentity());
		p = cb.and(p, cb.equal(root.get(Empower_.type), Empower.TYPE_APPLICATION));
		p = cb.and(p, cb.equal(root.get(Empower_.application), empower.getApplication()));
		p = cb.and(p, cb.notEqual(root.get(Empower_.id), empower.getId()));
		Long count = em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult();
		if (count > 0) {
			return true;
		} else {
			return false;
		}
	}

	private boolean typeProcessExist(Business business, Empower empower) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Empower.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Empower> root = cq.from(Empower.class);
		Predicate p = cb.equal(root.get(Empower_.fromIdentity), empower.getFromIdentity());
		p = cb.and(p, cb.equal(root.get(Empower_.type), Empower.TYPE_PROCESS));
		p = cb.and(p, cb.equal(root.get(Empower_.process), empower.getProcess()));
		p = cb.and(p, cb.notEqual(root.get(Empower_.id), empower.getId()));
		Long count = em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult();
		if (count > 0) {
			return true;
		} else {
			return false;
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