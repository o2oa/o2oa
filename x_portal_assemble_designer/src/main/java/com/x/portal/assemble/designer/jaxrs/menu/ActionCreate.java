package com.x.portal.assemble.designer.jaxrs.menu;

import com.google.gson.JsonElement;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.portal.assemble.designer.Business;
import com.x.portal.assemble.designer.wrapin.WrapInMenu;
import com.x.portal.core.entity.Menu;
import com.x.portal.core.entity.Portal;

class ActionCreate extends ActionBase {

	ActionResult<WrapOutId> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<WrapOutId> result = new ActionResult<>();
			WrapInMenu wrapIn = this.convertToWrapIn(jsonElement, WrapInMenu.class);
			Portal portal = emc.find(wrapIn.getPortal(), Portal.class);
			if (!business.portal().checkPermission(effectivePerson, portal)) {
				throw new InsufficientPermissionException(effectivePerson.getName());
			}
			emc.beginTransaction(Menu.class);
			Menu menu = inCopier.copy(wrapIn);
			this.checkName(business, menu);
			this.checkAlias(business, menu);
			emc.persist(menu, CheckPersistType.all);
			emc.commit();
			ApplicationCache.notify(Menu.class);
			WrapOutId wrap = new WrapOutId(menu.getId());
			result.setData(wrap);
			return result;
		}
	}

}