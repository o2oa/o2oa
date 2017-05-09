package com.x.portal.assemble.designer.jaxrs.templatepage;

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
import com.x.portal.assemble.designer.wrapin.WrapInTemplatePage;
import com.x.portal.core.entity.TemplatePage;

class ActionCreate extends ActionBase {

	ActionResult<WrapOutId> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<WrapOutId> result = new ActionResult<>();
			if (!business.isPortalManager(effectivePerson)) {
				throw new InsufficientPermissionException(effectivePerson.getName());
			}
			WrapInTemplatePage wrapIn = this.convertToWrapIn(jsonElement, WrapInTemplatePage.class);
			TemplatePage o = inCopier.copy(wrapIn);
			o.setCreatorPerson(effectivePerson.getName());
			o.setLastUpdatePerson(effectivePerson.getName());
			o.setLastUpdateTime(new Date());
			this.checkName(business, o);
			this.checkAlias(business, o);
			emc.beginTransaction(TemplatePage.class);
			emc.persist(o, CheckPersistType.all);
			emc.commit();
			ApplicationCache.notify(TemplatePage.class);
			WrapOutId wrap = new WrapOutId(o.getId());
			result.setData(wrap);
			return result;
		}
	}

}