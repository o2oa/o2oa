package com.x.organization.assemble.personal.jaxrs.trustlog;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.organization.core.entity.accredit.TrustLog;

class ActionDelete extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			TrustLog trustLog = emc.find(id, TrustLog.class);
			if (null == trustLog) {
				throw new ExceptionEntityNotExist(id, TrustLog.class);
			}
			if (effectivePerson.isNotManager()) {
				throw new ExceptionAccessDenied(effectivePerson, trustLog);
			}
			emc.beginTransaction(TrustLog.class);
			emc.remove(trustLog, CheckRemoveType.all);
			emc.commit();
			ApplicationCache.notify(TrustLog.class);
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapBoolean {

	}
}