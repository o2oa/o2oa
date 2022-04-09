package com.x.query.service.processing.jaxrs.touch;

import com.google.gson.JsonElement;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

class ActionTest extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionTest.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		System.out.println(jsonElement);
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		Wo wo = new Wo();
		result.setData(wo);
		return result;

	}

	public static class Wo extends WrapBoolean {
	}

}