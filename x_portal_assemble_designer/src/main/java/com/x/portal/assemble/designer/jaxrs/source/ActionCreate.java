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

class ActionCreate extends ActionBase {

	ActionResult<WrapOutId> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<WrapOutId> result = new ActionResult<>();
			WrapInSource wrapIn = this.convertToWrapIn(jsonElement, WrapInSource.class);
			Portal portal = emc.find(wrapIn.getPortal(), Portal.class);
			if (!business.portal().checkPermission(effectivePerson, portal)) {
				throw new InsufficientPermissionException(effectivePerson.getName());
			}
			emc.beginTransaction(Source.class);
			Source source = inCopier.copy(wrapIn);
			this.checkName(business, source);
			this.checkAlias(business, source);
			emc.persist(source, CheckPersistType.all);
			emc.commit();
			ApplicationCache.notify(Source.class);
			WrapOutId wrap = new WrapOutId(source.getId());
			result.setData(wrap);
			return result;
		}
	}

}