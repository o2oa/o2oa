package com.x.program.center.jaxrs.test;

import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;

class ActionTest1 extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {

		ActionResult<Wo> result = new ActionResult<>();

		return result;
	}

	public static class Wo extends GsonPropertyObject {

	}
}