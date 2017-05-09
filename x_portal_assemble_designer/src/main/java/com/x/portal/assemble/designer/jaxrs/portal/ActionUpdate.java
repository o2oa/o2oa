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
import com.x.portal.core.entity.Menu;
import com.x.portal.core.entity.Page;
import com.x.portal.core.entity.Portal;
import com.x.portal.core.entity.Script;
import com.x.portal.core.entity.Source;

class ActionUpdate extends ActionBase {
	ActionResult<WrapOutId> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement)
			throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Portal o = emc.find(id, Portal.class);
			if (null == o) {
				throw new PortalNotExistedException(id);
			}
			if (!business.portal().checkPermission(effectivePerson, o)) {
				throw new PortalInsufficientPermissionException(effectivePerson.getName(), o.getName(), o.getId());
			}
			emc.beginTransaction(Portal.class);
			WrapInPortal wrapIn = this.convertToWrapIn(jsonElement, WrapInPortal.class);
			updateCopier.copy(wrapIn, o);
			o.setLastUpdatePerson(effectivePerson.getName());
			o.setLastUpdateTime(new Date());
			this.checkName(business, o);
			this.checkAlias(business, o);
			emc.check(o, CheckPersistType.all);
			emc.commit();
			ApplicationCache.notify(Portal.class);
			ApplicationCache.notify(Menu.class);
			ApplicationCache.notify(Page.class);
			ApplicationCache.notify(Source.class);
			ApplicationCache.notify(Script.class);
			result.setData(new WrapOutId(o.getId()));
			return result;
		}
	}
}