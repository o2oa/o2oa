package com.x.processplatform.service.processing.jaxrs.test;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.processplatform.service.processing.ThisApplication;

class ActionTest4 extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		wo.setValue(ThisApplication.context().threadFactory().stop("112233"));
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapBoolean {

	}
}