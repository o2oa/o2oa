package com.x.portal.assemble.designer.jaxrs.script;

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
import com.x.portal.assemble.designer.wrapin.WrapInScript;
import com.x.portal.core.entity.Portal;
import com.x.portal.core.entity.Script;

class ActionUpdate extends ActionBase {
	ActionResult<WrapOutId> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement)
			throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Script script = emc.find(id, Script.class);
			if (null == script) {
				throw new ScriptNotExistedException(id);
			}
			Portal portal = emc.find(script.getPortal(), Portal.class);
			if (null == portal) {
				throw new PortalNotExistedException(script.getPortal());
			}
			if (!business.portal().checkPermission(effectivePerson, portal)) {
				throw new InsufficientPermissionException(effectivePerson.getName());
			}
			emc.beginTransaction(Script.class);
			WrapInScript wrapIn = this.convertToWrapIn(jsonElement, WrapInScript.class);
			updateCopier.copy(wrapIn, script);
			script.setLastUpdatePerson(effectivePerson.getName());
			script.setLastUpdateTime(new Date());
			this.checkName(business, script);
			this.checkAlias(business, script);
			this.checkDepend(business, script);
			emc.check(script, CheckPersistType.all);
			emc.commit();
			ApplicationCache.notify(Script.class);
			result.setData(new WrapOutId(script.getId()));
			return result;
		}
	}
}