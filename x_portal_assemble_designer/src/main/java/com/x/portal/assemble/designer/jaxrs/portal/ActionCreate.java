package com.x.portal.assemble.designer.jaxrs.portal;

import java.util.Date;

import com.google.gson.JsonElement;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.portal.assemble.designer.Business;
import com.x.portal.assemble.designer.wrapin.WrapInPortal;
import com.x.portal.core.entity.Portal;

class ActionCreate extends ActionBase {

	ActionResult<WrapOutId> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<WrapOutId> result = new ActionResult<>();
			WrapOutId wrap = new WrapOutId();
			WrapInPortal wrapIn = this.convertToWrapIn(jsonElement, WrapInPortal.class);
			if (!business.isPortalManager(effectivePerson)) {
				throw new InsufficientPermissionException(effectivePerson.getName());
			}
			emc.beginTransaction(Portal.class);
			Portal portal = new Portal();
			wrapIn.copyTo(portal);
			portal.setCreatorPerson(effectivePerson.getName());
			portal.setLastUpdatePerson(effectivePerson.getName());
			portal.setLastUpdateTime(new Date());
			this.checkName(business, portal);
			this.checkAlias(business, portal);
			emc.persist(portal, CheckPersistType.all);
			emc.commit();
			ApplicationCache.notify(Portal.class);
			wrap = new WrapOutId(portal.getId());
			result.setData(wrap);
			return result;
		}
	}

}