package com.x.program.center.jaxrs.collect;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.project.config.Collect;
import com.x.base.core.project.config.Person;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WrapString;

class ActionValidatePassword extends BaseAction {

	ActionResult<Wo> execute(JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		Wo wo = new Wo();
		String password = wi.getPassword();
		if (StringUtils.isEmpty(password)) {
			throw new ExceptionPasswordEmpty();
		}
		if (!password.matches(Person.DEFAULT_PASSWORDREGEX)) {
			wo.setValue(Person.DEFAULT_PASSWORDREGEXHINT);
		}
		result.setData(wo);
		return result;
	}

	public static class Wi extends Collect {

		private static final long serialVersionUID = 2474013087563030882L;

	}

	public static class Wo extends WrapString {

		private static final long serialVersionUID = -4278484613148265915L;
	}

}
