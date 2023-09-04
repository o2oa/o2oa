package com.x.portal.assemble.designer.jaxrs.portal;

import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.StringTools;
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.Portal;
import com.x.portal.core.entity.Portal_;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

abstract class BaseAction extends StandardJaxrsAction {

	static CacheCategory cache = new CacheCategory(Portal.class);

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

		if ((!effectivePerson.isSecurityManager()) && (!effectivePerson.isManager())
				&& (!business.organization().person().hasRole(effectivePerson, OrganizationDefinition.PortalManager))) {
//		if (!effectivePerson.isSecurityManager() && !business.isPortalManager(effectivePerson)) {
			p = cb.isMember(effectivePerson.getDistinguishedName(), root.get(Portal_.controllerList));
			p = cb.or(p, cb.equal(root.get(Portal_.creatorPerson), effectivePerson.getDistinguishedName()));
		}
		cq.select(root.get(Portal_.id)).where(p);
		return em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());
	}

	List<String> listEditableWithPortalCategory(Business business, EffectivePerson effectivePerson,
			String portalCategory) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Portal.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Portal> root = cq.from(Portal.class);
		Predicate p = cb.conjunction();
		if ((!effectivePerson.isManager())
				&& (!business.organization().person().hasRole(effectivePerson, OrganizationDefinition.PortalManager))) {
			p = cb.isMember(effectivePerson.getDistinguishedName(), root.get(Portal_.controllerList));
			p = cb.or(p, cb.equal(root.get(Portal_.creatorPerson), effectivePerson.getDistinguishedName()));
		}
		if(Portal.CATEGORY_DEFAULT.equals(portalCategory)){
			p = cb.and(p, cb.or(cb.equal(root.get(Portal_.portalCategory), Portal.CATEGORY_DEFAULT),
					cb.equal(cb.trim(root.get(Portal_.portalCategory)), ""),
					cb.isNull(root.get(Portal_.portalCategory))));
		}else {
			p = cb.and(p, cb.equal(root.get(Portal_.portalCategory), Objects.toString(portalCategory, "")));
		}
		cq.select(root.get(Portal_.id)).where(p);
		return em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());
	}

	List<Portal> listEditableObj(Business business, EffectivePerson effectivePerson, String name, String portalCategory) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Portal.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Portal> cq = cb.createQuery(Portal.class);
		Root<Portal> root = cq.from(Portal.class);
		Predicate p = cb.conjunction();
		if ((!effectivePerson.isSecurityManager()) && (!effectivePerson.isManager())
				&& (!business.organization().person().hasRole(effectivePerson, OrganizationDefinition.PortalManager))) {
			p = cb.isMember(effectivePerson.getDistinguishedName(), root.get(Portal_.controllerList));
			p = cb.or(p, cb.equal(root.get(Portal_.creatorPerson), effectivePerson.getDistinguishedName()));
		}
		if(StringUtils.isNotBlank(portalCategory)) {
			if (Portal.CATEGORY_DEFAULT.equals(portalCategory)) {
				p = cb.and(p, cb.or(cb.equal(root.get(Portal_.portalCategory), Portal.CATEGORY_DEFAULT),
						cb.equal(cb.trim(root.get(Portal_.portalCategory)), ""),
						cb.isNull(root.get(Portal_.portalCategory))));
			} else {
				p = cb.and(p, cb.equal(root.get(Portal_.portalCategory), portalCategory));
			}
		}
		if(StringUtils.isNotBlank(name)){
			name = StringTools.escapeSqlLikeKey(name);
			if (StringUtils.isNotEmpty(name)) {
				p = cb.and(p, cb.like(root.get(Portal_.name), "%" + name + "%"));
			}
		}
		cq.select(root).where(p);
		return em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());
	}

}
