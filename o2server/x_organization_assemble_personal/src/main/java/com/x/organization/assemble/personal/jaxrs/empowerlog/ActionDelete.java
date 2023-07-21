package com.x.organization.assemble.personal.jaxrs.empowerlog;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.organization.core.entity.accredit.EmpowerLog;

class ActionDelete extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			EmpowerLog empowerLog = emc.find(id, EmpowerLog.class);
			if (null == empowerLog) {
				throw new ExceptionEntityNotExist(id, EmpowerLog.class);
			}
			if (effectivePerson.isNotManager()) {
				throw new ExceptionAccessDenied(effectivePerson, empowerLog);
			}
			emc.beginTransaction(EmpowerLog.class);
			emc.remove(empowerLog, CheckRemoveType.all);
			emc.commit();
			CacheManager.notify(EmpowerLog.class);
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapBoolean {

	}
}