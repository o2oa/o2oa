package com.x.portal.assemble.surface.jaxrs.page;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.portal.assemble.surface.Business;
import com.x.portal.core.entity.Page;
import com.x.portal.core.entity.Portal;

import net.sf.ehcache.Element;

class ActionGetWithPortal extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, String portalFlag) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			String cacheKey = ApplicationCache.concreteCacheKey(this.getClass(), flag, portalFlag);
			Element element = pageCache.get(cacheKey);
			if ((null != element) && (null != element.getObjectValue())) {
				wo = (Wo) element.getObjectValue();
				Portal portal = business.portal().pick(wo.getPortal());
				if (!business.portal().visible(effectivePerson, portal)) {
					throw new ExceptionPortalAccessDenied(effectivePerson.getDistinguishedName(), portal.getName(),
							portal.getId());
				}
			} else {
				Portal portal = business.portal().pick(portalFlag);
				if (null == portal) {
					throw new ExceptionPortalNotExist(portalFlag);
				}
				if (!business.portal().visible(effectivePerson, portal)) {
					throw new ExceptionPortalAccessDenied(effectivePerson.getDistinguishedName(), portal.getName(),
							portal.getId());
				}
				Page page = business.page().pick(portal, flag);
				if (null == page) {
					throw new ExceptionPageNotExist(flag);
				}
				wo = Wo.copier.copy(page);
				wo.setData(page.getDataOrMobileData());
				pageCache.put(new Element(cacheKey, wo));
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