package com.x.program.center.jaxrs.invoke;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.program.center.core.entity.Invoke;

class ActionGet extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Invoke invoke = emc.flag(flag, Invoke.class );
			if (null == invoke) {
				throw new ExceptionInvokeNotExist(flag);
			}
			Wo wo = Wo.copier.copy(invoke);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends Invoke {

		private static final long serialVersionUID = -125007357898871894L;

		static WrapCopier<Invoke, Wo> copier = WrapCopierFactory.wo(Invoke.class, Wo.class, null,
				ListTools.toList(JpaObject.FieldsInvisible));
	}

}