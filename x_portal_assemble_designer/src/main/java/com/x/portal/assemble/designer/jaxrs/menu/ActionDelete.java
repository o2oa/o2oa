package com.x.portal.assemble.designer.jaxrs.menu;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutBoolean;
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.Menu;
import com.x.portal.core.entity.Portal;

class ActionDelete extends ActionBase {

	ActionResult<WrapOutBoolean> execute(EffectivePerson effectivePerson, String id) throws Exception {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
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
			emc.remove(menu, CheckRemoveType.all);
			emc.commit();
			ApplicationCache.notify(Menu.class);
			result.setData(WrapOutBoolean.trueInstance());
			return result;
		}
	}
}