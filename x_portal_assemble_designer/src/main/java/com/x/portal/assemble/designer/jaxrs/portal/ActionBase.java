package com.x.portal.assemble.designer.jaxrs.portal;

import java.util.List;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.project.jaxrs.AbstractJaxrsAction;
import com.x.portal.assemble.designer.Business;
import com.x.portal.assemble.designer.wrapin.WrapInPortal;
import com.x.portal.assemble.designer.wrapout.WrapOutPage;
import com.x.portal.assemble.designer.wrapout.WrapOutPortal;
import com.x.portal.assemble.designer.wrapout.WrapOutPortalSummary;
import com.x.portal.core.entity.Page;
import com.x.portal.core.entity.Portal;
import com.x.portal.core.entity.Portal_;

abstract class ActionBase extends AbstractJaxrsAction {

	// static Ehcache cache =
	// ApplicationCache.instance().getCache(Portal.class);

	static BeanCopyTools<Portal, WrapOutPortal> outCopier = BeanCopyToolsBuilder.create(Portal.class,
			WrapOutPortal.class, null, WrapOutPortal.Excludes);

	static BeanCopyTools<Portal, WrapOutPortalSummary> summaryOutCopier = BeanCopyToolsBuilder.create(Portal.class,
			WrapOutPortalSummary.class, null, WrapOutPortalSummary.Excludes);

	static BeanCopyTools<Page, WrapOutPage> pageOutCopier = BeanCopyToolsBuilder.create(Page.class, WrapOutPage.class,
			null, WrapOutPage.Excludes);

	static BeanCopyTools<WrapInPortal, Portal> inCopier = BeanCopyToolsBuilder.create(WrapInPortal.class, Portal.class,
			null, WrapInPortal.Excludes);

	static BeanCopyTools<WrapInPortal, Portal> updateCopier = BeanCopyToolsBuilder.create(WrapInPortal.class,
			Portal.class, null, JpaObject.FieldsUnmodifies);

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
			p = cb.isMember(effectivePerson.getName(), root.get(Portal_.controllerList));
			p = cb.or(p, cb.equal(root.get(Portal_.creatorPerson), effectivePerson.getName()));
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
			p = cb.isMember(effectivePerson.getName(), root.get(Portal_.controllerList));
			p = cb.or(p, cb.equal(root.get(Portal_.creatorPerson), effectivePerson.getName()));
		}
		p = cb.and(p, cb.equal(root.get(Portal_.portalCategory), Objects.toString(portalCategory, "")));
		cq.select(root.get(Portal_.id)).where(p).distinct(true);
		List<String> list = em.createQuery(cq.select(root.get(Portal_.id)).where(p)).getResultList();
		return list;
	}

}