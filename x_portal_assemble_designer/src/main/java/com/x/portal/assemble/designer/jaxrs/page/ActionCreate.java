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

class ActionCreate extends ActionBase {

	ActionResult<WrapOutId> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<WrapOutId> result = new ActionResult<>();
			WrapInPage wrapIn = this.convertToWrapIn(jsonElement, WrapInPage.class);
			Portal portal = emc.find(wrapIn.getPortal(), Portal.class);
			if (!business.portal().checkPermission(effectivePerson, portal)) {
				throw new InsufficientPermissionException(effectivePerson.getName());
			}
			emc.beginTransaction(Page.class);
			Page page = inCopier.copy(wrapIn);
			this.checkName(business, page);
			this.checkAlias(business, page);
			emc.persist(page, CheckPersistType.all);
			/** 更新首页 */
			if (this.isBecomeFirstPage(business, portal, page)) {
				emc.beginTransaction(Portal.class);
				portal.setFirstPage(page.getId());
			}
			emc.commit();
			ApplicationCache.notify(Page.class);
			ApplicationCache.notify(Portal.class);
			WrapOutId wrap = new WrapOutId(page.getId());
			result.setData(wrap);
			return result;
		}
	}

}