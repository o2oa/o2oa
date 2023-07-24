package com.x.program.center.jaxrs.collect;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.program.center.ThisApplication;
import com.x.program.center.schedule.Area;

class ActionSyncArea extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		wo.setValue(true);
		result.setData(wo);
		ThisApplication.context().scheduleLocal(Area.class);
		return result;
	}

	public static class Wo extends WrapBoolean {
	}

}
