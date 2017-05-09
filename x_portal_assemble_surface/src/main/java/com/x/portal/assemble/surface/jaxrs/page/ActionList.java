package com.x.portal.assemble.surface.jaxrs.page;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.portal.assemble.surface.Business;
import com.x.portal.assemble.surface.wrapout.WrapOutPage;
import com.x.portal.core.entity.Page;
import com.x.portal.core.entity.Portal;

class ActionList extends ActionBase {

	ActionResult<List<WrapOutPage>> execute(EffectivePerson effectivePerson, String portalId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<WrapOutPage>> result = new ActionResult<>();
			List<WrapOutPage> wraps = new ArrayList<>();
			Portal portal = business.portal().pick(portalId);
			if (null == portal) {
				throw new PortalNotExistedException(portalId);
			}
			if (!business.portal().visible(effectivePerson, portal)) {
				throw new PortalAccessDeniedException(effectivePerson.getName(), portal.getName(), portal.getId());
			}
			List<String> ids = business.page().listWithPortal(portal.getId());
			for (String id : ids) {
				Page o = business.page().pick(id);
				if (null == o) {
					throw new PageNotExistedException(id);
				} else {
					wraps.add(outCopier.copy(o));
				}
			}
			result.setData(wraps);
			return result;
		}
	}
}