package com.x.organization.assemble.personal.jaxrs.empower;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.organization.core.entity.accredit.Empower;

class ActionDelete extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Empower empower = emc.find(id, Empower.class);
			if (null == empower) {
				throw new ExceptionEntityNotExist(id, Empower.class);
			}
			if (effectivePerson.isNotManager() && effectivePerson.isNotPerson(empower.getFromPerson())) {
				throw new ExceptionAccessDenied(effectivePerson, empower);
			}
			emc.beginTransaction(Empower.class);
			emc.remove(empower, CheckRemoveType.all);
			emc.commit();
			ApplicationCache.notify(Empower.class);
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapBoolean {

	}
}