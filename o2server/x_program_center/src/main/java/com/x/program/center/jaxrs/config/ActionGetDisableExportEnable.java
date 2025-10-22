package com.x.program.center.jaxrs.config;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;

class ActionGetDisableExportEnable extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		wo.setValue(Config.general().getDisableExportEnable());
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapBoolean {

	}
}
