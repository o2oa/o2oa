package com.x.processplatform.assemble.designer.jaxrs.script;

import java.util.Date;

import com.google.gson.JsonElement;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.assemble.designer.wrapin.WrapInScript;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Script;

class ActionCreate extends ActionBase {
	ActionResult<WrapOutId> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutId> result = new ActionResult<>();
			WrapInScript wrapIn = this.convertToWrapIn(jsonElement, WrapInScript.class);
			WrapOutId wrap = new WrapOutId();
			Business business = new Business(emc);
			Application application = emc.find(wrapIn.getApplication(), Application.class);
			if (null == application) {
				throw new ApplicationNotExistedException(wrapIn.getApplication());
			}
			if (!business.applicationEditAvailable(effectivePerson, application)) {
				throw new ApplicationAccessDeniedException(effectivePerson.getName(), application.getName(),
						application.getId());
			}
			emc.beginTransaction(Script.class);
			Script script = new Script();
			inCopier.copy(wrapIn, script);
			script.setCreatorPerson(effectivePerson.getName());
			script.setLastUpdatePerson(effectivePerson.getName());
			script.setLastUpdateTime(new Date());
			emc.persist(script, CheckPersistType.all);
			emc.commit();
			ApplicationCache.notify(Script.class);
			wrap = new WrapOutId(script.getId());
			result.setData(wrap);
			return result;
		}
	}
}
