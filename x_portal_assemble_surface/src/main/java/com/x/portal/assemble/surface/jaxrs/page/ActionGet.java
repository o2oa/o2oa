package com.x.portal.assemble.surface.jaxrs.page;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.portal.assemble.surface.Business;
import com.x.portal.assemble.surface.wrapout.WrapOutPage;
import com.x.portal.core.entity.Page;
import com.x.portal.core.entity.Portal;

class ActionGet extends ActionBase {

	ActionResult<WrapOutPage> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<WrapOutPage> result = new ActionResult<>();
			Page page = business.page().pick(id);
			if (null == page) {
				throw new PageNotExistedException(id);
			}
			Portal portal = business.portal().pick(page.getPortal());
			if (null == portal) {
				throw new PortalNotExistedException(page.getPortal());
			}
			if (!business.portal().visible(effectivePerson, portal)) {
				throw new PortalAccessDeniedException(effectivePerson.getName(), portal.getName(), portal.getId());
			}
			WrapOutPage wrap = outCopier.copy(page);
			result.setData(wrap);
			return result;
		}
	}
}