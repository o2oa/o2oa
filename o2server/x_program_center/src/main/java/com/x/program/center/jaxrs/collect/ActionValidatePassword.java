package com.x.program.center.jaxrs.collect;

import com.x.base.core.project.tools.PasswordTools;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.project.config.Collect;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WrapInteger;

class ActionValidatePassword extends BaseAction {

	ActionResult<Wo> execute(JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		Wo wo = new Wo();
		String password = wi.getPassword();
		if (StringUtils.isEmpty(password)) {
			throw new ExceptionPasswordEmpty();
		}
		wo.setValue(PasswordTools.checkPasswordStrength(password));
		result.setData(wo);
		return result;
	}

	public static class Wi extends Collect {

	}

	public static class Wo extends WrapInteger {
	}

}
