package com.x.portal.assemble.surface.jaxrs.menu;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.portal.assemble.surface.Business;
import com.x.portal.assemble.surface.wrapout.WrapOutMenu;
import com.x.portal.core.entity.Menu;
import com.x.portal.core.entity.Portal;

class ActionGet extends ActionBase {

	ActionResult<WrapOutMenu> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<WrapOutMenu> result = new ActionResult<>();
			Menu menu = business.menu().pick(id);
			if (null == menu) {
				throw new MenuNotExistedException(id);
			}
			Portal portal = business.portal().pick(menu.getPortal());
			if (null == portal) {
				throw new PortalNotExistedException(menu.getPortal());
			}
			if (!business.portal().visible(effectivePerson, portal)) {
				throw new PortalAccessDeniedException(effectivePerson.getName(), portal.getName(), portal.getId());
			}
			WrapOutMenu wrap = outCopier.copy(menu);
			result.setData(wrap);
			return result;
		}
	}
}