package com.x.processplatform.assemble.designer.jaxrs.application;

import java.util.Date;

import com.google.gson.JsonElement;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.role.RoleDefinition;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.assemble.designer.wrapin.WrapInApplication;
import com.x.processplatform.core.entity.element.Application;

class ActionCreate extends ActionBase {

	ActionResult<WrapOutId> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutId> result = new ActionResult<>();
			WrapOutId wrap = new WrapOutId();
			WrapInApplication wrapIn = this.convertToWrapIn(jsonElement, WrapInApplication.class);
			Business business = new Business(emc);
			if ((!business.organization().role().hasAny(effectivePerson.getName(),
					RoleDefinition.ProcessPlatformCreator, RoleDefinition.ProcessPlatformManager,
					RoleDefinition.Manager)) & (!effectivePerson.isManager())) {
				throw new InsufficientPermissionException(effectivePerson.getName());
			}
			emc.beginTransaction(Application.class);
			Application application = new Application();
			wrapIn.copyTo(application);
			application.setCreatorPerson(effectivePerson.getName());
			application.setLastUpdatePerson(effectivePerson.getName());
			application.setLastUpdateTime(new Date());
			emc.persist(application, CheckPersistType.all);
			emc.commit();
			ApplicationCache.notify(Application.class);
			wrap = new WrapOutId(application.getId());
			result.setData(wrap);
			return result;
		}
	}

}