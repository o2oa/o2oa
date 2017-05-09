package com.x.portal.assemble.designer.jaxrs.portal;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.portal.assemble.designer.Business;
import com.x.portal.assemble.designer.wrapout.WrapOutPortal;
import com.x.portal.core.entity.Portal;

class ActionGet extends ActionBase {
	ActionResult<WrapOutPortal> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<WrapOutPortal> result = new ActionResult<>();
			WrapOutPortal wrap = null;
			// String cacheKey = ApplicationCache.concreteCacheKey(id);
			// Element element = cache.get(cacheKey);
			Portal o = emc.find(id, Portal.class);
			if (null == o) {
				throw new PortalNotExistedException(id);
			}
			if (!business.portal().editable(effectivePerson, o)) {
				throw new InvisibleException(effectivePerson.getName(), o.getName(), o.getId());
			}
			wrap = outCopier.copy(o);
			result.setData(wrap);
			return result;
		}
	}

}