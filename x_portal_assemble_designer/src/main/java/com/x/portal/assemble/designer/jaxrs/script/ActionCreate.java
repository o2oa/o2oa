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

class ActionCreate extends ActionBase {

	ActionResult<WrapOutId> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<WrapOutId> result = new ActionResult<>();
			WrapInScript wrapIn = this.convertToWrapIn(jsonElement, WrapInScript.class);
			Portal portal = emc.find(wrapIn.getPortal(), Portal.class);
			if (!business.portal().checkPermission(effectivePerson, portal)) {
				throw new InsufficientPermissionException(effectivePerson.getName());
			}
			emc.beginTransaction(Script.class);
			Script script = inCopier.copy(wrapIn);
			script.setCreatorPerson(effectivePerson.getName());
			script.setLastUpdatePerson(effectivePerson.getName());
			script.setLastUpdateTime(new Date());
			this.checkName(business, script);
			this.checkAlias(business, script);
			this.checkDepend(business, script);
			emc.persist(script, CheckPersistType.all);
			emc.commit();
			ApplicationCache.notify(Script.class);
			WrapOutId wrap = new WrapOutId(script.getId());
			result.setData(wrap);
			return result;
		}
	}

}