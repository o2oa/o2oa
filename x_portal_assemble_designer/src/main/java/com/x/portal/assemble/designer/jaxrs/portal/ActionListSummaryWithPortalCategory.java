package com.x.portal.assemble.designer.jaxrs.portal;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.utils.SortTools;
import com.x.portal.assemble.designer.Business;
import com.x.portal.assemble.designer.wrapout.WrapOutPortalSummary;
import com.x.portal.core.entity.Page;
import com.x.portal.core.entity.Portal;
import com.x.portal.core.entity.Portal_;

class ActionListSummaryWithPortalCategory extends ActionBase {

	ActionResult<List<WrapOutPortalSummary>> execute(EffectivePerson effectivePerson, String portalCategory)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<WrapOutPortalSummary>> result = new ActionResult<>();
			List<WrapOutPortalSummary> wraps = new ArrayList<>();
			Business business = new Business(emc);
			List<String> ids = this.listEditableWithPortalCategory(business, effectivePerson, portalCategory);
			/* 由于有多值字段所以需要全部取出 */
			for (Portal o : emc.list(Portal.class, ids)) {
				WrapOutPortalSummary wrap = summaryOutCopier.copy(o);
				List<String> os = business.page().listWithPortal(o.getId());
				wrap.setPageList(pageOutCopier.copy(emc.list(Page.class, os)));
				wraps.add(wrap);
			}
			SortTools.asc(wraps, "name");
			result.setData(wraps);
			return result;
		}
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