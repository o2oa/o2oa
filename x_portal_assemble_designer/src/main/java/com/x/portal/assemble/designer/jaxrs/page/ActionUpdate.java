package com.x.portal.assemble.designer.jaxrs.page;

import com.google.gson.JsonElement;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.portal.assemble.designer.Business;
import com.x.portal.assemble.designer.wrapin.WrapInPage;
import com.x.portal.core.entity.Page;
import com.x.portal.core.entity.Portal;

class ActionUpdate extends ActionBase {
	ActionResult<WrapOutId> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement)
			throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Page page = emc.find(id, Page.class);
			if (null == page) {
				throw new PageNotExistedException(id);
			}
			Portal portal = emc.find(page.getPortal(), Portal.class);
			if (null == portal) {
				throw new PortalNotExistedException(page.getPortal());
			}
			if (!business.portal().checkPermission(effectivePerson, portal)) {
				throw new InsufficientPermissionException(effectivePerson.getName());
			}
			emc.beginTransaction(Page.class);
			WrapInPage wrapIn = this.convertToWrapIn(jsonElement, WrapInPage.class);
			updateCopier.copy(wrapIn, page);
			this.checkName(business, page);
			this.checkAlias(business, page);
			emc.check(page, CheckPersistType.all);
			/** 更新首页 */
			if (this.isBecomeFirstPage(business, portal, page)) {
				emc.beginTransaction(Portal.class);
				portal.setFirstPage(page.getId());
			}
			emc.commit();
			ApplicationCache.notify(Page.class);
			ApplicationCache.notify(Portal.class);
			result.setData(new WrapOutId(page.getId()));
			return result;
		}
	}
}