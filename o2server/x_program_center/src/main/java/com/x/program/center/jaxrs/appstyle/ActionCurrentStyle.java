package com.x.program.center.jaxrs.appstyle;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.AppStyle;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.portal.core.entity.Page;
import com.x.portal.core.entity.Page_;
import com.x.portal.core.entity.Portal;
import com.x.portal.core.entity.Portal_;
import com.x.program.center.Business;

class ActionCurrentStyle extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			AppStyle appStyle = Config.appStyle();
			Wo wo = Wo.copier.copy(appStyle);
			if (StringUtils.equals(appStyle.getIndexType(), AppStyle.INDEXTYPE_PORTAL)) {
				Portal portal = business.entityManagerContainer().flag(appStyle.getIndexPortal(), Portal.class);
				if (null != portal && StringUtils.isNotEmpty(portal.getFirstPage())) {
					Page page = business.entityManagerContainer().find(portal.getFirstPage(), Page.class);
					// 设置了indexPortal就不判断hasMobile
					if (null != page) {
						wo.setIndexType(AppStyle.INDEXTYPE_PORTAL);
						wo.setIndexPortal(portal.getId());
						// 兼容值后期废弃
						wo.setIndexId(portal.getId());
					}
				}
			}
			List<Portal> os = this.listMobilePortal(business);
			wo.setPortalList(WoPortal.copier.copy(os));
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends AppStyle {

		private static final long serialVersionUID = 1100871693389441652L;

		// 兼容值后期废弃
		private String indexId;

		static WrapCopier<AppStyle, Wo> copier = WrapCopierFactory.wo(AppStyle.class, Wo.class, null,
				ListTools.toList(JpaObject.FieldsInvisible));

		@FieldDescribe("门户列表")
		private List<WoPortal> portalList = new ArrayList<>();

		public List<WoPortal> getPortalList() {
			return portalList;
		}

		public void setPortalList(List<WoPortal> portalList) {
			this.portalList = portalList;
		}

		public String getIndexId() {
			return indexId;
		}

		public void setIndexId(String indexId) {
			this.indexId = indexId;
		}
	}

	public static class WoPortal extends Portal {

		private static final long serialVersionUID = -6937619512225630470L;

		static WrapCopier<Portal, WoPortal> copier = WrapCopierFactory.wo(Portal.class, WoPortal.class,
				JpaObject.singularAttributeField(Portal.class, true, true), null);

	}

	List<Portal> listMobilePortal(Business business) throws Exception {
		List<String> pageIds = this.listMobilePage(business);
		if (!pageIds.isEmpty()) {
			EntityManager em = business.entityManagerContainer().get(Portal.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Portal> cq = cb.createQuery(Portal.class);
			Root<Portal> root = cq.from(Portal.class);
			Predicate p = root.get(Portal_.firstPage).in(pageIds);
			p = cb.and(p, cb.or(cb.isNull(root.get(Portal_.mobileClient)), cb.isTrue(root.get(Portal_.mobileClient))));
			List<Portal> os = em.createQuery(cq.select(root).where(p)).getResultList();
			os = os.stream().sorted(Comparator.comparing(Portal::getUpdateTime, Comparator.nullsLast(Date::compareTo)))
					.collect(Collectors.toList());
			return os;
		} else {
			return new ArrayList<Portal>();
		}
	}

	private List<String> listMobilePage(Business business) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Page.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Page> root = cq.from(Page.class);
		// Predicate p = cb.conjunction();
		Predicate p = cb.equal(root.get(Page_.hasMobile), true);
		List<String> os = em.createQuery(cq.select(root.get(Page_.id)).where(p)).getResultList();
		return os;
	}

}