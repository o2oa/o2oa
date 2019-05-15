package com.x.organization.assemble.personal.jaxrs.trust;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.organization.core.entity.accredit.Trust;

class ActionDelete extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Trust trust = emc.find(id, Trust.class);
			if (null == trust) {
				throw new ExceptionEntityNotExist(id, Trust.class);
			}
			if (effectivePerson.isNotManager() && effectivePerson.isNotPerson(trust.getFromPerson())) {
				throw new ExceptionAccessDenied(effectivePerson, trust);
			}
			emc.beginTransaction(Trust.class);
			emc.remove(trust, CheckRemoveType.all);
			emc.commit();
			ApplicationCache.notify(Trust.class);
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapBoolean {

	}
}