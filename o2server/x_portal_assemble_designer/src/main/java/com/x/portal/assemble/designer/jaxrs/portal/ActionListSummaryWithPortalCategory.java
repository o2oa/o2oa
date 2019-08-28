package com.x.portal.assemble.designer.jaxrs.portal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.Page;
import com.x.portal.core.entity.Portal;
import com.x.portal.core.entity.Portal_;

class ActionListSummaryWithPortalCategory extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String portalCategory) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			List<String> ids = this.listEditableWithPortalCategory(business, effectivePerson, portalCategory);
			/* 由于有多值字段所以需要全部取出 */
			List<Wo> wos = Wo.copier.copy(emc.list(Portal.class, ids));
			for (Wo wo : wos) {
				List<String> os = business.page().listWithPortal(wo.getId());
				wo.setPageList(emc.fetch(os, WoPage.copier));
			}
			wos = wos.stream().sorted(Comparator.comparing(Wo::getName, Comparator.nullsLast(String::compareTo)))
					.collect(Collectors.toList());
			result.setData(wos);
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
			p = cb.isMember(effectivePerson.getDistinguishedName(), root.get(Portal_.controllerList));
			p = cb.or(p, cb.equal(root.get(Portal_.creatorPerson), effectivePerson.getDistinguishedName()));
		}
		p = cb.and(p, cb.equal(root.get(Portal_.portalCategory), Objects.toString(portalCategory, "")));
		cq.select(root.get(Portal_.id)).where(p).distinct(true);
		List<String> list = em.createQuery(cq.select(root.get(Portal_.id)).where(p)).getResultList();
		return list;
	}

	public static class Wo extends Portal {

		private static final long serialVersionUID = 4546727999450453639L;

		static WrapCopier<Portal, Wo> copier = WrapCopierFactory.wo(Portal.class, Wo.class,
				JpaObject.singularAttributeField(Portal.class, true, true), null);

		private List<WoPage> pageList = new ArrayList<>();

		public List<WoPage> getPageList() {
			return pageList;
		}

		public void setPageList(List<WoPage> pageList) {
			this.pageList = pageList;
		}
	}

	public static class WoPage extends Page {

		private static final long serialVersionUID = -8790557719118069443L;

		static WrapCopier<Page, WoPage> copier = WrapCopierFactory.wo(Page.class, WoPage.class,
				JpaObject.singularAttributeField(Page.class, true, false), null);
	}

}