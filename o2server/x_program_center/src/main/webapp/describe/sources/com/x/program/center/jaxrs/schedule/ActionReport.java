package com.x.program.center.jaxrs.schedule;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.program.center.core.entity.ScheduleLog;

class ActionReport extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			if (effectivePerson.isNotManager()) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			ActionResult<Wo> result = new ActionResult<>();
			emc.beginTransaction(ScheduleLog.class);
			ScheduleLog log = Wi.copier.copy(wi);
			emc.persist(log, CheckPersistType.all);
			emc.commit();
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends ScheduleLog {

		private static final long serialVersionUID = 1996856138701159925L;
		static WrapCopier<Wi, ScheduleLog> copier = WrapCopierFactory.wi(Wi.class, ScheduleLog.class, null,
				JpaObject.FieldsUnmodify);
	}

	public static class Wo extends WrapBoolean {
	}

}
