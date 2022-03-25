package com.x.portal.assemble.surface.jaxrs.page;

import java.util.Optional;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.portal.assemble.surface.Business;
import com.x.portal.core.entity.Page;
import com.x.portal.core.entity.Portal;

class ActionGetWithPortalMobile extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, String portalFlag) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			CacheKey cacheKey = new CacheKey(this.getClass(), flag, portalFlag);
			Optional<?> optional = CacheManager.get(pageCache, cacheKey);
			if (optional.isPresent()) {
				wo = (Wo) optional.get();
				Portal portal = business.portal().pick(wo.getPortal());
				if (isNotLoginPage(flag) && (!business.portal().visible(effectivePerson, portal))) {
					throw new ExceptionPortalAccessDenied(effectivePerson.getDistinguishedName(), portal.getName(),
							portal.getId());
				}
			} else {
				Portal portal = business.portal().pick(portalFlag);
				if (null == portal) {
					throw new ExceptionPortalNotExist(portalFlag);
				}
				if (isNotLoginPage(flag) && (!business.portal().visible(effectivePerson, portal))) {
					throw new ExceptionPortalAccessDenied(effectivePerson.getDistinguishedName(), portal.getName(),
							portal.getId());
				}
				Page page = business.page().pick(portal, flag);
				wo = Wo.copier.copy(page);
				wo.setData(page.getMobileDataOrData());
				CacheManager.put(pageCache, cacheKey, wo);
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends Page {

		private static final long serialVersionUID = 3454132769791427909L;
		static WrapCopier<Page, Wo> copier = WrapCopierFactory.wo(Page.class, Wo.class, null,
				ListTools.toList(JpaObject.FieldsInvisible, Page.data_FIELDNAME, Page.mobileData_FIELDNAME));

	}
}