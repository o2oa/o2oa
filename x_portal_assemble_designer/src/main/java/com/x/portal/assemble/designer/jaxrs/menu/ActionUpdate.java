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

class ActionUpdate extends ActionBase {
	ActionResult<WrapOutId> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement)
			throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Menu menu = emc.find(id, Menu.class);
			if (null == menu) {
				throw new MenuNotExistedException(id);
			}
			Portal portal = emc.find(menu.getPortal(), Portal.class);
			if (null == portal) {
				throw new PortalNotExistedException(menu.getPortal());
			}
			if (!business.portal().checkPermission(effectivePerson, portal)) {
				throw new InsufficientPermissionException(effectivePerson.getName());
			}
			emc.beginTransaction(Menu.class);
			WrapInMenu wrapIn = this.convertToWrapIn(jsonElement, WrapInMenu.class);
			updateCopier.copy(wrapIn, menu);
			this.checkName(business, menu);
			this.checkAlias(business, menu);
			emc.check(menu, CheckPersistType.all);
			emc.commit();
			ApplicationCache.notify(Menu.class);
			result.setData(new WrapOutId(menu.getId()));
			return result;
		}
	}
}