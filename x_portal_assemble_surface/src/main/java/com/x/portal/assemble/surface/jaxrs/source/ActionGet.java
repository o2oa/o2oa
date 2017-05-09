package com.x.portal.assemble.surface.jaxrs.source;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.portal.assemble.surface.Business;
import com.x.portal.assemble.surface.wrapout.WrapOutSource;
import com.x.portal.core.entity.Portal;
import com.x.portal.core.entity.Source;

class ActionGet extends ActionBase {

	ActionResult<WrapOutSource> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<WrapOutSource> result = new ActionResult<>();
			Source source = business.source().pick(id);
			if (null == source) {
				throw new SourceNotExistedException(id);
			}
			Portal portal = business.portal().pick(source.getPortal());
			if (null == portal) {
				throw new PortalNotExistedException(source.getPortal());
			}
			if (!business.portal().visible(effectivePerson, portal)) {
				throw new PortalAccessDeniedException(effectivePerson.getName(), portal.getName(), portal.getId());
			}
			WrapOutSource wrap = outCopier.copy(source);
			result.setData(wrap);
			return result;
		}
	}
}