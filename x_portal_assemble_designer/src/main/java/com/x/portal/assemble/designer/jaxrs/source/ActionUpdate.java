package com.x.portal.assemble.designer.jaxrs.source;

import com.google.gson.JsonElement;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.portal.assemble.designer.Business;
import com.x.portal.assemble.designer.wrapin.WrapInSource;
import com.x.portal.core.entity.Portal;
import com.x.portal.core.entity.Source;

class ActionUpdate extends ActionBase {
	ActionResult<WrapOutId> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement)
			throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
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
			WrapInSource wrapIn = this.convertToWrapIn(jsonElement, WrapInSource.class);
			updateCopier.copy(wrapIn, source);
			this.checkName(business, source);
			this.checkAlias(business, source);
			emc.check(source, CheckPersistType.all);
			emc.commit();
			ApplicationCache.notify(Source.class);
			result.setData(new WrapOutId(source.getId()));
			return result;
		}
	}
}