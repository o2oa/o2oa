package com.x.processplatform.assemble.designer.jaxrs.application;

import java.util.Date;

import com.google.gson.JsonElement;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.assemble.designer.wrapin.WrapInApplication;
import com.x.processplatform.core.entity.element.Application;

class ActionUpdate extends ActionBase {

	ActionResult<WrapOutId> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutId> result = new ActionResult<>();
			WrapOutId wrap = new WrapOutId();
			WrapInApplication wrapIn = this.convertToWrapIn(jsonElement, WrapInApplication.class);
			Business business = new Business(emc);
			emc.beginTransaction(Application.class);
			Application application = emc.find(id, Application.class);
			if (null == application) {
				throw new ApplicationNotExistedException(id);
			}
			if (!business.applicationEditAvailable(effectivePerson, application)) {
				throw new InsufficientPermissionException(effectivePerson.getName());
			}
			inCopier.copy(wrapIn, application);
			application.setLastUpdatePerson(effectivePerson.getName());
			application.setLastUpdateTime(new Date());
			emc.commit();
			ApplicationCache.notify(Application.class);
			wrap = new WrapOutId(application.getId());
			result.setData(wrap);
			return result;
		}
	}

}