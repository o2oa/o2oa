package com.x.portal.assemble.designer.jaxrs.source;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutBoolean;
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.Portal;
import com.x.portal.core.entity.Source;

class ActionDelete extends ActionBase {

	ActionResult<WrapOutBoolean> execute(EffectivePerson effectivePerson, String id) throws Exception {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Source source = emc.find(id, Source.class);
			if (null == source) {
				throw new SourceNotExistedException(id);
			}
			Portal portal = emc.find(source.getPortal(), Portal.class);
			if (null == portal) {
				throw new PortalNotExistedException(source.getPortal());
			}
			if (!business.portal().checkPermission(effectivePerson, portal)) {
				throw new InsufficientPermissionException(effectivePerson.getName());
			}
			emc.beginTransaction(Source.class);
			emc.remove(source, CheckRemoveType.all);
			emc.commit();
			ApplicationCache.notify(Source.class);
			result.setData(WrapOutBoolean.trueInstance());
			return result;
		}
	}
}