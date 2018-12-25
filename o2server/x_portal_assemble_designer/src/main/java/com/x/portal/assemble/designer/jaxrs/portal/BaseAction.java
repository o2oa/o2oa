package com.x.portal.assemble.designer.jaxrs.portal;

import java.util.List;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.Portal;
import com.x.portal.core.entity.Portal_;

import net.sf.ehcache.Ehcache;

abstract class BaseAction extends StandardJaxrsAction {

	static Ehcache cache = ApplicationCache.instance().getCache(Portal.class);

	void checkName(Business business, Portal portal) throws Exception {
		if (StringUtils.isEmpty(portal.getName())) {
			throw new NameEmptyException();
		}
		String id = business.portal().getWithName(portal.getName());
		if (StringUtils.isNotEmpty(id) && (!StringUtils.equals(portal.getId(), id))) {
			throw new NameDuplicateException(portal.getName());
		}
		id = business.portal().getWithAlias(portal.getName());
		if (StringUtils.isNotEmpty(id) && (!StringUtils.equals(portal.getId(), id))) {
			throw new NameDuplicateWithAliasException(portal.getName());
		}
	}

	void checkAlias(Business business, Portal portal) throws Exception {
		if (StringUtils.isEmpty(portal.getAlias())) {
			return;
		}
		String id = business.portal().getWithAlias(portal.getAlias());
		if (StringUtils.isNotEmpty(id) && (!StringUtils.equals(portal.getId(), id))) {
			throw new AliasDuplicateException(portal.getAlias());
		}
		id = business.portal().getWithName(portal.getAlias());
		if (StringUtils.isNotEmpty(id) && (!StringUtils.equals(portal.getId(), id))) {
			throw new AliasDuplicateWithNameException(portal.getAlias());
		}
	}

	List<String> listEditable(Business business, EffectivePerson effectivePerson) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Portal.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Portal> root = cq.from(Portal.class);
		Predicate p = cb.conjunction();
		if (!business.isPortalManager(effectivePerson)) {
			p = cb.isMember(effectivePerson.getDistinguishedName(), root.get(Portal_.controllerList));
			p = cb.or(p, cb.equal(root.get(Portal_.creatorPerson), effectivePerson.getDistinguishedName()));
		}
		cq.select(root.get(Portal_.id)).where(p).distinct(true);
		List<String> list = em.createQuery(cq.select(root.get(Portal_.id)).where(p)).getResultList();
		return list;
	}

	List<String> listEditableWithPortalCategory(Business business, EffectivePerson effectivePerson,
			String portalCategory) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Portal.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Portal> root = cq.from(Portal.class);
		Predicate p = cb.conjunction();
		if (!business.isPortalManager(effectivePerson)) {
			p = cb.isMember(effectivePerson.getDistinguishedName(), root.get(Portal_.controllerList));
			p = cb.or(p, cb.equal(root.get(Portal_.creatorPerson), effectivePerson.getDistinguishedName()));
		}
		p = cb.and(p, cb.equal(root.get(Portal_.portalCategory), Objects.toString(portalCategory, "")));
		cq.select(root.get(Portal_.id)).where(p).distinct(true);
		List<String> list = em.createQuery(cq.select(root.get(Portal_.id)).where(p)).getResultList();
		return list;
	}

}