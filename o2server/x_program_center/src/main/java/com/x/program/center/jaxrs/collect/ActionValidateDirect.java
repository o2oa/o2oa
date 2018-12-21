package com.x.program.center.jaxrs.collect;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.project.config.Collect;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WrapBoolean;

class ActionValidateDirect extends BaseAction {

	ActionResult<Wo> execute(JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		Wo wo = new Wo();
		wo.setValue(true);
		if (!this.connect()) {
			throw new ExceptionUnableConnect();
		}
		String name = wi.getName();
		String password = wi.getPassword();
		if (StringUtils.isEmpty(name)) {
			throw new ExceptionNameEmpty();
		}
		if (StringUtils.isEmpty(password)) {
			throw new ExceptionPasswordEmpty();
		}
		if (!this.validate(name, password)) {
			wo.setValue(false);
		}
		result.setData(wo);
		return result;
	}

	public static class Wi extends Collect {

	}

	public static class Wo extends WrapBoolean {
	}

}
