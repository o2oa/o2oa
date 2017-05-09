package com.x.portal.assemble.surface.jaxrs.script;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.portal.assemble.surface.Business;
import com.x.portal.assemble.surface.wrapout.WrapOutScript;
import com.x.portal.core.entity.Portal;
import com.x.portal.core.entity.Script;

class ActionGet extends ActionBase {

	ActionResult<WrapOutScript> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<WrapOutScript> result = new ActionResult<>();
			Script script = business.script().pick(id);
			if (null == script) {
				throw new ScriptNotExistedException(id);
			}
			Portal portal = business.portal().pick(script.getPortal());
			if (null == portal) {
				throw new PortalNotExistedException(script.getPortal());
			}
			if (!business.portal().visible(effectivePerson, portal)) {
				throw new PortalAccessDeniedException(effectivePerson.getName(), portal.getName(), portal.getId());
			}
			WrapOutScript wrap = outCopier.copy(script);
			result.setData(wrap);
			return result;
		}
	}
}