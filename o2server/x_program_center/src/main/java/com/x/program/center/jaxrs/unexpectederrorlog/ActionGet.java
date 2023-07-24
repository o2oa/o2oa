package com.x.program.center.jaxrs.unexpectederrorlog;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.program.center.core.entity.UnexpectedErrorLog;

class ActionGet extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			UnexpectedErrorLog o = emc.find(id, UnexpectedErrorLog.class);
			if (null == o) {
				throw new ExceptionEntityNotExist(id, UnexpectedErrorLog.class);
			}
			Wo wo = Wo.copier.copy(o);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends UnexpectedErrorLog {

		private static final long serialVersionUID = -1984264806526036338L;
		static WrapCopier<UnexpectedErrorLog, Wo> copier = WrapCopierFactory.wo(UnexpectedErrorLog.class, Wo.class,
				null, JpaObject.FieldsInvisible);

	}
}
