package com.x.program.center.jaxrs.invoke;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.program.center.core.entity.Invoke;

class ActionDelete extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Invoke invoke = emc.flag(flag, Invoke.class );
			if (null == invoke) {
				throw new ExceptionInvokeNotExist(flag);
			}
			emc.beginTransaction(Invoke.class);
			emc.remove(invoke, CheckRemoveType.all);
			emc.commit();
			Wo wo = new Wo();
			wo.setId(invoke.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

	}
}