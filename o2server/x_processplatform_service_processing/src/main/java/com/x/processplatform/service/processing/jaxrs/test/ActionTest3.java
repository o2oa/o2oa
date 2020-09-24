package com.x.processplatform.service.processing.jaxrs.test;

import java.util.Map;
import java.util.TreeMap;

import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.processplatform.service.processing.ThisApplication;

class ActionTest3 extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		Map<Object, Object> value = ThisApplication.context().threadFactory().parameter("112233").orElse(null);
		if (null != value) {
			wo = XGsonBuilder.convert(value, Wo.class);
		}
		result.setData(wo);
		return result;
	}

	public static class Wo extends TreeMap<Object, Object> {

	}

}